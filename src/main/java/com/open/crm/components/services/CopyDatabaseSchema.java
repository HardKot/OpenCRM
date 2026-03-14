package com.open.crm.components.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CopyDatabaseSchema {

    private final DataSource dataSource;

    public void execute(String sourceSchema, String targetSchema) throws Exception {
        log.info("Copying schema '{}' → '{}'", sourceSchema, targetSchema);

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                createSchema(conn, targetSchema);
                copySequences(conn, sourceSchema, targetSchema);
                copyTables(conn, sourceSchema, targetSchema);
                fixSequenceDefaults(conn, sourceSchema, targetSchema);
                fixSequenceOwnership(conn, sourceSchema, targetSchema);
                copyForeignKeys(conn, sourceSchema, targetSchema);
                copyViews(conn, sourceSchema, targetSchema);
                copyData(conn, sourceSchema, targetSchema);
                resetSequences(conn, targetSchema);
                conn.commit();
                log.info("Schema copy '{}' → '{}' completed successfully", sourceSchema, targetSchema);
            }
            catch (Exception e) {
                conn.rollback();
                log.error("Schema copy '{}' → '{}' failed, rolling back: {}", sourceSchema, targetSchema,
                        e.getMessage());
                throw e;
            }
        }
    }

    private void createSchema(Connection conn, String schema) throws SQLException {
        execute(conn, String.format("CREATE SCHEMA IF NOT EXISTS \"%s\"", schema));
        log.debug("Schema '{}' created (or already existed)", schema);
    }

    private void copySequences(Connection conn, String from, String to) throws SQLException {
        String sql = """
                SELECT sequence_name,
                       data_type,
                       start_value,
                       minimum_value,
                       maximum_value,
                       increment,
                       cycle_option
                FROM information_schema.sequences
                WHERE sequence_schema = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String seqName = rs.getString("sequence_name");
                    String dataType = rs.getString("data_type");
                    String start = rs.getString("start_value");
                    String min = rs.getString("minimum_value");
                    String max = rs.getString("maximum_value");
                    String incr = rs.getString("increment");
                    boolean cycle = "YES".equalsIgnoreCase(rs.getString("cycle_option"));

                    String ddl = String.format(
                            "CREATE SEQUENCE IF NOT EXISTS \"%s\".\"%s\" AS %s "
                                    + "START %s MINVALUE %s MAXVALUE %s INCREMENT %s %s",
                            to, seqName, dataType, start, min, max, incr, cycle ? "CYCLE" : "NO CYCLE");

                    execute(conn, ddl);
                    log.debug("Created sequence '{}'", seqName);
                }
            }
        }
    }

    private void copyTables(Connection conn, String from, String to) throws SQLException {
        String sql = """
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = ?
                  AND table_type = 'BASE TABLE'
                ORDER BY table_name
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String table = rs.getString("table_name");
                    String ddl = String.format("CREATE TABLE \"%s\".\"%s\" (LIKE \"%s\".\"%s\" INCLUDING ALL)", to,
                            table, from, table);
                    execute(conn, ddl);
                    log.debug("Created table '{}'", table);
                }
            }
        }
    }

    private void fixSequenceDefaults(Connection conn, String from, String to) throws SQLException {
        String sql = """
                SELECT table_name, column_name, column_default
                FROM information_schema.columns
                WHERE table_schema = ?
                  AND column_default LIKE 'nextval(%'
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String table = rs.getString("table_name");
                    String column = rs.getString("column_name");
                    String oldDef = rs.getString("column_default");
                    String newDef = oldDef.replace("\"" + from + "\".", "\"" + to + "\".")
                        .replace("'" + from + ".", "'" + to + ".");

                    if (!newDef.equals(oldDef)) {
                        String ddl = String.format("ALTER TABLE \"%s\".\"%s\" ALTER COLUMN \"%s\" SET DEFAULT %s", to,
                                table, column, newDef);
                        execute(conn, ddl);
                        log.debug("Fixed sequence default for '{}.{}'", table, column);
                    }
                }
            }
        }
    }

    private void fixSequenceOwnership(Connection conn, String from, String to) throws SQLException {
        String sql = """
                SELECT seq.relname  AS seq_name,
                       tab.relname  AS table_name,
                       col.attname  AS column_name
                FROM   pg_class          seq
                JOIN   pg_namespace      seq_ns  ON seq_ns.oid   = seq.relnamespace
                JOIN   pg_depend         dep     ON dep.objid    = seq.oid
                JOIN   pg_class          tab     ON tab.oid      = dep.refobjid
                JOIN   pg_namespace      tab_ns  ON tab_ns.oid   = tab.relnamespace
                JOIN   pg_attribute      col     ON col.attrelid = tab.oid
                                                 AND col.attnum  = dep.refobjsubid
                WHERE  seq.relkind   = 'S'
                  AND  dep.deptype   = 'a'
                  AND  seq_ns.nspname = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String seq = rs.getString("seq_name");
                    String table = rs.getString("table_name");
                    String column = rs.getString("column_name");

                    String ddl = String.format("ALTER SEQUENCE \"%s\".\"%s\" OWNED BY \"%s\".\"%s\".\"%s\"", to, seq,
                            to, table, column);
                    execute(conn, ddl);
                    log.debug("Set ownership of sequence '{}' → '{}.{}'", seq, table, column);
                }
            }
        }
    }

    private void copyForeignKeys(Connection conn, String from, String to) throws SQLException {
        String sql = """
                SELECT rc.constraint_name,
                       kcu.table_name          AS src_table,
                       kcu.column_name         AS src_column,
                       kcu.ordinal_position,
                       ccu.table_schema        AS ref_schema,
                       ccu.table_name          AS ref_table,
                       ccu.column_name         AS ref_column,
                       rc.delete_rule,
                       rc.update_rule,
                       rc.match_option
                FROM   information_schema.referential_constraints rc
                JOIN   information_schema.key_column_usage kcu
                       ON  kcu.constraint_name  = rc.constraint_name
                       AND kcu.constraint_schema = rc.constraint_schema
                JOIN   information_schema.constraint_column_usage ccu
                       ON  ccu.constraint_name  = rc.unique_constraint_name
                       AND ccu.constraint_schema = rc.unique_constraint_schema
                WHERE  rc.constraint_schema = ?
                ORDER  BY rc.constraint_name, kcu.ordinal_position
                """;

        Map<String, FkInfo> fks = new LinkedHashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("constraint_name");
                    String srcTable = rs.getString("src_table");
                    String srcCol = rs.getString("src_column");
                    String refSchema = rs.getString("ref_schema");
                    String refTable = rs.getString("ref_table");
                    String refCol = rs.getString("ref_column");
                    String onDelete = rs.getString("delete_rule");
                    String onUpdate = rs.getString("update_rule");

                    fks.computeIfAbsent(name, k -> new FkInfo(srcTable, refSchema, refTable, onDelete, onUpdate))
                        .addColumn(srcCol, refCol);
                }
            }
        }

        for (Map.Entry<String, FkInfo> entry : fks.entrySet()) {
            FkInfo fk = entry.getValue();

            String targetRefSchema = from.equals(fk.refSchema) ? to : fk.refSchema;

            String srcCols = fk.srcColumns.stream().map(c -> "\"" + c + "\"").reduce((a, b) -> a + ", " + b).orElse("");
            String refCols = fk.refColumns.stream().map(c -> "\"" + c + "\"").reduce((a, b) -> a + ", " + b).orElse("");

            String ddl = String.format(
                    "ALTER TABLE \"%s\".\"%s\" " + "ADD CONSTRAINT \"%s\" " + "FOREIGN KEY (%s) "
                            + "REFERENCES \"%s\".\"%s\" (%s) " + "ON DELETE %s ON UPDATE %s",
                    to, fk.srcTable, entry.getKey(), srcCols, targetRefSchema, fk.refTable, refCols, fk.onDelete,
                    fk.onUpdate);

            execute(conn, ddl);
            log.debug("Created FK '{}' on table '{}'", entry.getKey(), fk.srcTable);
        }
    }

    private void copyViews(Connection conn, String from, String to) throws SQLException {
        String sql = """
                SELECT table_name, view_definition
                FROM information_schema.views
                WHERE table_schema = ?
                ORDER BY table_name
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String viewName = rs.getString("table_name");
                    String def = rs.getString("view_definition")
                        .replace("\"" + from + "\".", "\"" + to + "\".")
                        .replace(from + ".", to + ".");

                    String ddl = String.format("CREATE OR REPLACE VIEW \"%s\".\"%s\" AS %s", to, viewName, def);
                    execute(conn, ddl);
                    log.debug("Created view '{}'", viewName);
                }
            }
        }
    }

    private void copyData(Connection conn, String from, String to) throws SQLException {
        String sql = """
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = ?
                  AND table_type = 'BASE TABLE'
                ORDER BY table_name
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String table = rs.getString("table_name");
                    String ddl = String.format("INSERT INTO \"%s\".\"%s\" SELECT * FROM \"%s\".\"%s\"", to, table, from,
                            table);
                    execute(conn, ddl);
                    log.debug("Copied data for table '{}'", table);
                }
            }
        }
    }

    private void resetSequences(Connection conn, String schema) throws SQLException {
        String sql = """
                SELECT seq.relname  AS seq_name,
                       tab.relname  AS table_name,
                       col.attname  AS column_name
                FROM   pg_class          seq
                JOIN   pg_namespace      seq_ns  ON seq_ns.oid   = seq.relnamespace
                JOIN   pg_depend         dep     ON dep.objid    = seq.oid
                JOIN   pg_class          tab     ON tab.oid      = dep.refobjid
                JOIN   pg_namespace      tab_ns  ON tab_ns.oid   = tab.relnamespace
                JOIN   pg_attribute      col     ON col.attrelid = tab.oid
                                                 AND col.attnum  = dep.refobjsubid
                WHERE  seq.relkind   = 'S'
                  AND  dep.deptype   = 'a'
                  AND  seq_ns.nspname = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, schema);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String seqName = rs.getString("seq_name");
                    String tableName = rs.getString("table_name");
                    String columnName = rs.getString("column_name");
                    String ddl = String.format("SELECT setval('\"" + schema + "\".\"" + seqName + "\"', "
                            + "COALESCE((SELECT MAX(\"" + columnName + "\") FROM \"" + schema + "\".\"" + tableName
                            + "\"), 1), " + "(SELECT MAX(\"" + columnName + "\") FROM \"" + schema + "\".\"" + tableName
                            + "\") IS NOT NULL)");
                    execute(conn, ddl);
                    log.debug("Reset sequence '{}' for '{}.{}'", seqName, tableName, columnName);
                }
            }
        }
    }

    private void execute(Connection conn, String sql) throws SQLException {
        log.trace("Executing SQL: {}", sql);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static class FkInfo {

        final String srcTable;

        final String refSchema;

        final String refTable;

        final String onDelete;

        final String onUpdate;

        final List<String> srcColumns = new ArrayList<>();

        final List<String> refColumns = new ArrayList<>();

        FkInfo(String srcTable, String refSchema, String refTable, String onDelete, String onUpdate) {
            this.srcTable = srcTable;
            this.refSchema = refSchema;
            this.refTable = refTable;
            this.onDelete = onDelete;
            this.onUpdate = onUpdate;
        }

        void addColumn(String src, String ref) {
            srcColumns.add(src);
            refColumns.add(ref);
        }

    }

}
