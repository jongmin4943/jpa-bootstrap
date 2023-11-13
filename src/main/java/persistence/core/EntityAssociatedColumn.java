package persistence.core;

import jakarta.persistence.FetchType;

import java.util.List;

public interface EntityAssociatedColumn extends EntityColumn {

    default EntityMetadata<?> getAssociatedEntityMetadata() {
        return EntityMetadata.from(this.getJoinColumnType());
    }

    default EntityColumns getAssociatedEntityColumns() {
        return getAssociatedEntityMetadata().getColumns();
    }

    default List<String> getAssociatedEntityColumnNamesWithAlias() {
        return getAssociatedEntityMetadata().getColumnNamesWithAlias();
    }

    String getNameWithAliasAssociatedEntity();

    default String getAssociatedEntityTableName() {
        return getAssociatedEntityMetadata().getTableName();
    }

    FetchType getFetchType();

    default boolean isFetchTypeLazy() {
        return this.getFetchType().equals(FetchType.LAZY);
    }

    default boolean isFetchTypeEager() {
        return this.getFetchType().equals(FetchType.EAGER);
    }

    Class<?> getJoinColumnType();

}
