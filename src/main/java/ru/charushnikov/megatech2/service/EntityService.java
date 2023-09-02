package ru.charushnikov.megatech2.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.charushnikov.megatech2.entity.AbstractEntity;
import ru.charushnikov.megatech2.repository.EntityRepository;

@Service
@AllArgsConstructor
public abstract class EntityService<E extends AbstractEntity, R extends EntityRepository<E>> implements CommonService<E> {
    private final R repository;
}
