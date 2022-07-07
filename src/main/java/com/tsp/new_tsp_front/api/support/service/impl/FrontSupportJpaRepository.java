package com.tsp.new_tsp_front.api.support.service.impl;

import com.tsp.new_tsp_front.api.support.domain.FrontSupportDTO;
import com.tsp.new_tsp_front.api.support.domain.FrontSupportEntity;
import com.tsp.new_tsp_front.api.support.mapper.SupportMapper;
import com.tsp.new_tsp_front.exception.ApiExceptionType;
import com.tsp.new_tsp_front.exception.TspException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.tsp.new_tsp_front.api.support.mapper.SupportMapper.INSTANCE;
import static com.tsp.new_tsp_front.exception.ApiExceptionType.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FrontSupportJpaRepository {
	private final EntityManager em;

	/**
	 * <pre>
	 * 1. MethodName : insertSupportModel
	 * 2. ClassName  : FrontSupportJpaRepository.java
	 * 3. Comment    : 프론트 모델 지원하기
	 * 4. 작성자       : CHO
	 * 5. 작성일       : 2022. 01. 09.
	 * </pre>
	 */
	@Modifying(clearAutomatically = true)
	@Transactional
	public FrontSupportDTO insertSupportModel(FrontSupportEntity frontSupportEntity) throws TspException {
		try {
			em.persist(frontSupportEntity);

			return INSTANCE.toDto(frontSupportEntity);
		} catch (Exception e) {
			throw new TspException(ERROR_MODEL, e);
		}
	}
}
