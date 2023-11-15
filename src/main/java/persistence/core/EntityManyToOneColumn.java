package persistence.core;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

public class EntityManyToOneColumn implements EntityAssociatedColumn {
    private static final String DELIMITER = "_";
    private final EntityColumn column;
    private final boolean isNotNull;
    private final boolean isInsertable;
    private final FetchType fetchType;
    private final Class<?> joinColumnType;
    private final String joinColumnName;
    private final EntityMetadata<?> associatedEntityMetadata;

    public EntityManyToOneColumn(final Field field, final String tableName) {
        field.setAccessible(true);
        this.column = new EntityFieldColumn(field, tableName);
        this.isNotNull = initIsNotNull(field);
        this.isInsertable = initIsInsertable(field);
        this.fetchType = initFetchType(field);
        this.joinColumnType = field.getType();
        this.associatedEntityMetadata = EntityMetadata.from(this.joinColumnType);
        this.joinColumnName = initJoinColumnName(field);
    }

    private boolean initIsNotNull(final Field field) {
        final JoinColumn columnMetadata = field.getDeclaredAnnotation(JoinColumn.class);
        return Optional.ofNullable(columnMetadata)
                .map(column -> !column.nullable())
                .orElse(false);
    }

    private boolean initIsInsertable(final Field field) {
        final JoinColumn columnMetadata = field.getDeclaredAnnotation(JoinColumn.class);
        return Optional.ofNullable(columnMetadata)
                .map(JoinColumn::insertable)
                .orElse(true);
    }

    private FetchType initFetchType(final Field field) {
        final ManyToOne columnMetadata = field.getDeclaredAnnotation(ManyToOne.class);
        return Optional.ofNullable(columnMetadata)
                .map(ManyToOne::fetch)
                .orElse(FetchType.EAGER);
    }

    private String initJoinColumnName(final Field field) {
        final JoinColumn columnMetadata = field.getDeclaredAnnotation(JoinColumn.class);
        return Optional.ofNullable(columnMetadata)
                .map(JoinColumn::name)
                .orElseGet(()->guessJoinColumnName(field));
    }

    private String guessJoinColumnName(final Field field) {
        final EntityMetadata<?> entityMetadata = getAssociatedEntityMetadata();
        return field.getName() + DELIMITER + entityMetadata.getIdColumnName();
    }

    public String getAssociatedEntityIdColumnNameWithAlias() {
        return getAssociatedEntityMetadata().getIdColumnNameWithAlias();
    }

    @Override
    public EntityMetadata<?> getAssociatedEntityMetadata() {
        return this.associatedEntityMetadata;
    }

    @Override
    public String getNameWithAliasAssociatedEntity() {
        return this.getTableName() + ALIAS_DELIMITER + this.getName();
    }

    @Override
    public FetchType getFetchType() {
        return this.fetchType;
    }

    @Override
    public Class<?> getJoinColumnType() {
        return this.joinColumnType;
    }

    @Override
    public String getTableName() {
        return this.column.getTableName();
    }

    @Override
    public String getName() {
        return this.joinColumnName;
    }

    @Override
    public boolean isNotNull() {
        return this.isNotNull;
    }

    @Override
    public Class<?> getType() {
        return this.column.getType();
    }

    @Override
    public boolean isStringValued() {
        return false;
    }

    @Override
    public int getStringLength() {
        return this.column.getStringLength();
    }

    @Override
    public String getFieldName() {
        return this.column.getFieldName();
    }

    @Override
    public boolean isInsertable() {
        return this.isInsertable;
    }

    @Override
    public boolean isAutoIncrement() {
        return false;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final EntityManyToOneColumn that = (EntityManyToOneColumn) object;
        return isNotNull == that.isNotNull && isInsertable == that.isInsertable && Objects.equals(column, that.column) && fetchType == that.fetchType && Objects.equals(joinColumnType, that.joinColumnType) && Objects.equals(joinColumnName, that.joinColumnName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, isNotNull, isInsertable, fetchType, joinColumnType, joinColumnName);
    }

}
