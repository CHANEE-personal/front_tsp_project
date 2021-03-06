package com.tsp.new_tsp_front.api.model.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tsp.new_tsp_front.api.model.domain.FrontModelDTO;
import com.tsp.new_tsp_front.api.model.domain.FrontModelEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.tsp.new_tsp_front.api.common.domain.QCommonImageEntity.commonImageEntity;
import static com.tsp.new_tsp_front.api.model.domain.QFrontModelEntity.frontModelEntity;
import static com.tsp.new_tsp_front.api.model.service.impl.ModelMapper.INSTANCE;
import static com.tsp.new_tsp_front.common.utils.StringUtil.getInt;
import static com.tsp.new_tsp_front.common.utils.StringUtil.getString;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FrontModelJpaRepository {
    private final JPAQueryFactory queryFactory;

    private BooleanExpression searchModel(Map<String, Object> modelMap) {
        String searchType = getString(modelMap.get("searchType"), "");
        String searchKeyword = getString(modelMap.get("searchKeyword"), "");
        Integer categoryCd = getInt(modelMap.get("categoryCd"), 0);

        if ("0".equals(searchType)) {
            return frontModelEntity.modelKorName.contains(searchKeyword)
                    .or(frontModelEntity.modelEngName.contains(searchKeyword)
                            .or(frontModelEntity.modelDescription.contains(searchKeyword)))
                    .and(frontModelEntity.categoryCd.eq(categoryCd));
        } else if ("1".equals(searchType)) {
            return frontModelEntity.modelKorName.contains(searchKeyword)
                    .or(frontModelEntity.modelEngName.contains(searchKeyword))
                    .and(frontModelEntity.categoryCd.eq(categoryCd));
        } else {
            if (!"".equals(searchKeyword)) {
                return frontModelEntity.modelDescription.contains(searchKeyword).and(frontModelEntity.categoryCd.eq(categoryCd));
            } else {
                return frontModelEntity.categoryCd.eq(categoryCd);
            }
        }
    }

    /**
     * <pre>
     * 1. MethodName : getMainModelList
     * 2. ClassName  : FrontModelJpaRepository.java
     * 3. Comment    : ????????? ?????? ?????? ????????? ??????
     * 4. ?????????       : CHO
     * 5. ?????????       : 2022. 03. 27.
     * </pre>
     */
    public List<FrontModelDTO> getMainModelList() {
        List<FrontModelEntity> modelList = queryFactory
                .selectFrom(frontModelEntity)
                .orderBy(frontModelEntity.idx.desc())
                .leftJoin(frontModelEntity.commonImageEntityList, commonImageEntity)
                .fetchJoin()
                .where(frontModelEntity.modelMainYn.eq("Y")
                        .and(frontModelEntity.visible.eq("Y")
                                .and(commonImageEntity.typeName.eq("model")
                                        .and(commonImageEntity.imageType.eq("main"))
                                        .and(commonImageEntity.visible.eq("Y")))))
                .fetch();

        modelList.forEach(list -> modelList.get(modelList.indexOf(list)).setRnum(modelList.indexOf(list)));

        return INSTANCE.toDtoList(modelList);
    }

    /**
     * <pre>
     * 1. MethodName : getModelCount
     * 2. ClassName  : FrontModelJpaRepository.java
     * 3. Comment    : ????????? ?????? ????????? ?????? ??????
     * 4. ?????????       : CHO
     * 5. ?????????       : 2022. 03. 27.
     * </pre>
     */
    public int getModelCount(Map<String, Object> modelMap) {
        return queryFactory
                .selectFrom(frontModelEntity)
                .where(searchModel(modelMap)
                        .and(frontModelEntity.visible.eq("Y")))
                .fetch().size();
    }

    /**
     * <pre>
     * 1. MethodName : getModelList
     * 2. ClassName  : FrontModelJpaRepository.java
     * 3. Comment    : ????????? ?????? ????????? ??????
     * 4. ?????????       : CHO
     * 5. ?????????       : 2022. 01. 02.
     * </pre>
     */
    public List<FrontModelDTO> getModelList(Map<String, Object> modelMap) {
        List<FrontModelEntity> modelList = queryFactory
                .selectFrom(frontModelEntity)
                .orderBy(frontModelEntity.idx.desc())
                .leftJoin(frontModelEntity.commonImageEntityList, commonImageEntity)
                .fetchJoin()
                .where(searchModel(modelMap).and(frontModelEntity.visible.eq("Y")))
                .offset(getInt(modelMap.get("jpaStartPage"), 0))
                .limit(getInt(modelMap.get("size"), 0))
                .fetch();

        modelList.forEach(list -> modelList.get(modelList.indexOf(list))
                .setRnum(getInt(modelMap.get("startPage"), 1) * (getInt(modelMap.get("size"), 1)) - (2 - modelList.indexOf(list))));

        return INSTANCE.toDtoList(modelList);
    }

    /**
     * <pre>
     * 1. MethodName : getModelInfo
     * 2. ClassName  : FrontModelJpaRepository.java
     * 3. Comment    : ????????? ?????? ?????? ??????
     * 4. ?????????       : CHO
     * 5. ?????????       : 2022. 01. 09.
     * </pre>
     */
    public FrontModelDTO getModelInfo(FrontModelEntity existFrontModelEntity) {
        //?????? ?????? ??????
        FrontModelEntity getModelInfo = queryFactory
                .selectFrom(frontModelEntity)
                .leftJoin(frontModelEntity.commonImageEntityList, commonImageEntity)
                .fetchJoin()
                .where(frontModelEntity.idx.eq(existFrontModelEntity.getIdx())
                        .and(frontModelEntity.visible.eq("Y"))
                        .and(commonImageEntity.typeName.eq("model")))
                .fetchOne();

        return INSTANCE.toDto(getModelInfo);
    }
}
