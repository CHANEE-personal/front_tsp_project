package com.tsp.new_tsp_front.api.portfolio.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tsp.new_tsp_front.api.portfolio.domain.FrontPortFolioDTO;
import com.tsp.new_tsp_front.api.portfolio.domain.FrontPortFolioEntity;
import com.tsp.new_tsp_front.common.utils.StringUtil;
import com.tsp.new_tsp_front.exception.ApiExceptionType;
import com.tsp.new_tsp_front.exception.TspException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.tsp.new_tsp_front.api.common.domain.QCommonImageEntity.commonImageEntity;
import static com.tsp.new_tsp_front.api.portfolio.domain.QFrontPortFolioEntity.frontPortFolioEntity;

@Repository
@RequiredArgsConstructor
public class FrontPortFolioJpaRepository {

	private final JPAQueryFactory queryFactory;

	private BooleanExpression searchPortFolio(Map<String, Object> modelMap) {
		String searchType = StringUtil.getString(modelMap.get("searchType"),"");
		String searchKeyword = StringUtil.getString(modelMap.get("searchKeyword"),"");

		if ("0".equals(searchType)) {
			return frontPortFolioEntity.title.contains(searchKeyword)
					.or(frontPortFolioEntity.description.contains(searchKeyword));
		} else if ("1".equals(searchType)) {
			return frontPortFolioEntity.title.contains(searchKeyword);
		} else {
			return frontPortFolioEntity.description.contains(searchKeyword);
		}
	}

	/**
	 * <pre>
	 * 1. MethodName : getPortFolioList
	 * 2. ClassName  : FrontPortFolioList.java
	 * 3. Comment    : 포트폴리오 리스트 조회
	 * 4. 작성자       : CHO
	 * 5. 작성일       : 2022. 01. 11.
	 * </pre>
	 *
	 * @param portFolioMap
	 */
	public List<FrontPortFolioDTO> getPortFolioList(Map<String, Object> portFolioMap) {

		try {
			List<FrontPortFolioEntity> portFolioList = queryFactory
					.selectFrom(frontPortFolioEntity)
					.where(searchPortFolio(portFolioMap))
					.orderBy(frontPortFolioEntity.idx.desc())
					.offset(StringUtil.getInt(portFolioMap.get("jpaStartPage"),0))
					.limit(StringUtil.getInt(portFolioMap.get("size"),0))
					.fetch();

			List<FrontPortFolioDTO> portFolioDtoList = PortFolioMapper.INSTANCE.toDtoList(portFolioList);

			for(int i = 0; i < portFolioDtoList.size(); i++) {
				portFolioDtoList.get(i).setRnum(StringUtil.getInt(portFolioMap.get("startPage"),1)*(StringUtil.getInt(portFolioMap.get("size"),1))-(2-i));
			}

			return portFolioDtoList;
		} catch (Exception e) {
			throw new TspException(ApiExceptionType.NOT_FOUND_PORTFOLIO_LIST);
		}
	}

	/**
	 * <pre>
	 * 1. MethodName : getPortFolioInfo
	 * 2. ClassName  : FrontPortFolioList.java
	 * 3. Comment    : 포트폴리오 상세 조회
	 * 4. 작성자       : CHO
	 * 5. 작성일       : 2022. 01. 12.
	 * </pre>
	 *
	 * @param existFrontPortFolioEntity
	 */
	public FrontPortFolioDTO getPortFolioInfo(FrontPortFolioEntity existFrontPortFolioEntity) {

		try {
			FrontPortFolioEntity getPortFolioInfo = queryFactory
					.selectFrom(frontPortFolioEntity)
					.orderBy(frontPortFolioEntity.idx.desc())
					.leftJoin(frontPortFolioEntity.commonImageEntityList, commonImageEntity)
					.fetchJoin()
					.where(frontPortFolioEntity.idx.eq(existFrontPortFolioEntity.getIdx())
							.and(frontPortFolioEntity.visible.eq("Y"))
							.and(commonImageEntity.typeName.eq("portfolio")))
					.fetchOne();

//			List<CommonImageEntity> getPortFolioImageList = queryFactory
//					.selectFrom(commonImageEntity)
//					.where(commonImageEntity.typeName.eq("portfolio")
//							.and(commonImageEntity.typeIdx.eq(existFrontPortFolioEntity.getIdx())
//									.and(commonImageEntity.visible.eq("Y"))))
//					.fetch();
//
//			portFolioMap.put("portFolioMap", PortFolioMapper.INSTANCE.toDto(getPortFolioInfo));
//			portFolioMap.put("portFolioImageList", ModelImageMapper.INSTANCE.toDtoList(getPortFolioImageList));

			return PortFolioMapper.INSTANCE.toDto(getPortFolioInfo);
		} catch (Exception e) {
			throw new TspException(ApiExceptionType.NOT_FOUND_PORTFOLIO);
		}
	}
}
