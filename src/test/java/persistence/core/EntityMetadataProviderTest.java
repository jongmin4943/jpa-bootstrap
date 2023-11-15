package persistence.core;

import domain.FixtureAssociatedEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.FixtureEntity;
import persistence.Application;
import persistence.exception.PersistenceException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityMetadataProviderTest {

    private static EntityMetadataProvider entityMetadataProvider;
    private Class<?> mockClass;

    @BeforeAll
    static void beforeAll() {
        entityMetadataProvider = EntityMetadataProvider.from(new EntityScanner(Application.class));
    }

    @Test
    @DisplayName("EntityMetadataProvider 를 통해 EntityMetadata 를 조회할 수 있다.")
    void entityMetadataProviderTest() {
        mockClass = FixtureEntity.WithId.class;
        final EntityMetadata<?> entityMetadata = entityMetadataProvider.getEntityMetadata(mockClass);
        assertSoftly(softly -> {
            softly.assertThat(entityMetadata).isNotNull();
            softly.assertThat(entityMetadata.getTableName()).isEqualTo("WithId");
            softly.assertThat(entityMetadata.getIdColumnName()).isEqualTo("id");
        });
    }

    @Test
    @DisplayName("EntityMetadataProvider 에 @Entity 가 붙어있지 않은 클래스를 조회할 시 Exception이 던져진다.")
    void entityMetadataProviderFailureTest() {
        mockClass = FixtureEntity.WithoutEntity.class;
        assertThatThrownBy(() -> entityMetadataProvider.getEntityMetadata(mockClass))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("EntityMetadataProvider 를 통해 조회된 같은 타입의 EntityMetadata 는 같은 객체이다.")
    void entityMetadataCacheTest() {
        mockClass = FixtureEntity.WithId.class;
        final EntityMetadata<?> entityMetadata = entityMetadataProvider.getEntityMetadata(mockClass);
        final EntityMetadata<?> entityMetadataV2 = entityMetadataProvider.getEntityMetadata(mockClass);
        assertThat(entityMetadata == entityMetadataV2).isTrue();
    }

    @Test
    @DisplayName("getOneToManyAssociatedEntitiesMetadata(EntityMetadata) 를 통해 EntityMetadata 를 OneToMany 연관관계로 가진 EntityMetadata 들 전부를 반환받을 수 있다.")
    void getAllAssociatedEntitiesMetadataTest() {
        mockClass = FixtureAssociatedEntity.WithId.class;
        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);

        final Set<EntityMetadata<?>> allAssociatedEntitiesMetadata = entityMetadataProvider.getOneToManyAssociatedEntitiesMetadata(entityMetadata);

        assertThat(allAssociatedEntitiesMetadata).hasSize(6);
    }

    @Test
    @DisplayName("getAllEntityClasses 를 통해 모든 Entity 의 class 정보를 반환받을 수 있다.")
    void getAllEntityClassesTest() {
        final Set<Class<?>> allAssociatedEntitiesMetadata = entityMetadataProvider.getAllEntityClasses();

        assertThat(allAssociatedEntitiesMetadata).isNotEmpty();
    }
}
