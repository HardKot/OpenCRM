import { InvestigationLogDto } from "#shared/api";
import { useI18n } from "#shared/hooks";
import { Text, TextPropsWithoutChildren } from "#shared/ui";
import { createContext, PropsWithChildren, useContext, useMemo } from "react";

interface InvestigationLogItemProps extends PropsWithChildren {
  data: InvestigationLogDto;
}

const InvestigationLogContext = createContext<Partial<InvestigationLogDto>>({});

const InvestigationLogId = (props: TextPropsWithoutChildren) => {
  const { id } = useContext(InvestigationLogContext);
  if (!id) return null;
  return <Text {...props}>{id}</Text>;
};

const InvestigationLogCreateAt = (props: TextPropsWithoutChildren) => {
  const { createdAt } = useContext(InvestigationLogContext);
  const { strftime } = useI18n();

  const value = useMemo(() => {
    if (!createdAt) return null;
    return new Date(createdAt);
  }, [createdAt]);

  if (!value) return null;

  return <Text {...props}>{strftime(value, "%Y-%m-%d %H:%M:%S")}</Text>;
};

const InvestigationLogAuthor = (props: TextPropsWithoutChildren) => {
  const { author } = useContext(InvestigationLogContext);
  if (!author) return null;
  return <Text {...props}>{author.label}</Text>;
};

const InvestigationLogDetails = (props: TextPropsWithoutChildren) => {
  const { details } = useContext(InvestigationLogContext);
  if (!details) return null;
  return <Text {...props}>{details.description}</Text>;
};

const InvestigationLogAction = (props: TextPropsWithoutChildren) => {
  const { details } = useContext(InvestigationLogContext);
  const { t } = useI18n();
  if (!details?.action) return null;
  return (
    <Text {...props}>
      {t(`investigationLog.details.action.${details.action}`)}
    </Text>
  );
};

const InvestigationLogEntity = (props: TextPropsWithoutChildren) => {
  const { details } = useContext(InvestigationLogContext);
  const { t } = useI18n();
  if (!details?.entityName) return null;
  return (
    <Text {...props}>
      {t(`investigationLog.details.entity.${details.entityName}`)} (
      {t("investigationLog.details.entity.id", { count: details.entityId })})
    </Text>
  );
};

const InvestigationLogItem = ({
  data,
  children,
}: InvestigationLogItemProps) => (
  <InvestigationLogContext.Provider value={data}>
    {children}
  </InvestigationLogContext.Provider>
);

InvestigationLogItem.Id = InvestigationLogId;
InvestigationLogItem.CreatedAt = InvestigationLogCreateAt;
InvestigationLogItem.Action = InvestigationLogAction;
InvestigationLogItem.Author = InvestigationLogAuthor;
InvestigationLogItem.Details = InvestigationLogDetails;
InvestigationLogItem.Entity = InvestigationLogEntity;

export { InvestigationLogItem };
