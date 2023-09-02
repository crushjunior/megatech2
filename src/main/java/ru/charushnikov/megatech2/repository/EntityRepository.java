package ru.charushnikov.megatech2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.charushnikov.megatech2.entity.AbstractEntity;

@NoRepositoryBean
public interface EntityRepository<E extends AbstractEntity> extends JpaRepository<E, Long> {
}
