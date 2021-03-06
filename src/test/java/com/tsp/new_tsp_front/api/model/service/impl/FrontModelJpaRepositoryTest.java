package com.tsp.new_tsp_front.api.model.service.impl;

import com.tsp.new_tsp_front.api.common.domain.CommonImageDTO;
import com.tsp.new_tsp_front.api.common.domain.CommonImageEntity;
import com.tsp.new_tsp_front.api.model.domain.FrontModelDTO;
import com.tsp.new_tsp_front.api.model.domain.FrontModelEntity;
import com.tsp.new_tsp_front.exception.TspException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.util.*;

import static com.tsp.new_tsp_front.api.model.domain.FrontModelEntity.builder;
import static com.tsp.new_tsp_front.api.model.service.impl.ModelMapper.INSTANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;


@DataJpaTest
@Transactional
@TestPropertySource(locations = "classpath:application-local.properties")
@TestConstructor(autowireMode = ALL)
@RequiredArgsConstructor
@AutoConfigureTestDatabase(replace = NONE)
@ExtendWith(MockitoExtension.class)
@DisplayName("모델 Repository Test")
class FrontModelJpaRepositoryTest {
    @Mock private FrontModelJpaRepository mockFrontModelJpaRepository;
    private final FrontModelJpaRepository frontModelJpaRepository;

    private FrontModelEntity frontModelEntity;
    private FrontModelDTO frontModelDTO;
    private CommonImageEntity commonImageEntity;
    private CommonImageDTO commonImageDTO;
    List<CommonImageEntity> commonImageEntityList = new ArrayList<>();

    private void createModel() {
        frontModelEntity = builder()
                .categoryCd(1)
                .categoryAge("2")
                .modelKorFirstName("조")
                .modelKorSecondName("찬희")
                .modelKorName("조찬희")
                .modelFirstName("CHO")
                .modelSecondName("CHANHEE")
                .modelEngName("CHOCHANHEE")
                .modelDescription("chaneeCho")
                .modelMainYn("Y")
                .height("170")
                .size3("34-24-34")
                .shoes("270")
                .visible("Y")
                .build();

        frontModelDTO = INSTANCE.toDto(frontModelEntity);

        commonImageEntity = CommonImageEntity.builder()
                .idx(1)
                .imageType("main")
                .fileName("test.jpg")
                .fileMask("test.jpg")
                .filePath("/test/test.jpg")
                .typeIdx(1)
                .typeName("model")
                .build();

        commonImageDTO = ModelImageMapperImpl.INSTANCE.toDto(commonImageEntity);
    }

    @BeforeEach
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        createModel();
    }

    @Test
    @DisplayName("모델 리스트 갯수 조회 테스트")
    void 모델리스트갯수조회테스트() {
        // 정상
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("categoryCd", 1);

        // then
        assertThat(frontModelJpaRepository.getModelCount(modelMap)).isPositive();
    }

    @Test
    @DisplayName("모델 리스트 조회 테스트")
    void 모델리스트조회테스트() {
        // given
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("categoryCd", 1);
        modelMap.put("jpaStartPage", 1);
        modelMap.put("size", 3);

        // then
        assertThat(frontModelJpaRepository.getModelList(modelMap)).isNotEmpty();
    }

    @Test
    @DisplayName("모델 리스트 조회 예외 테스트")
    void 모델리스트조회예외테스트() {
        // given
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("categoryCd", -1);

        // then
        assertThatThrownBy(() -> frontModelJpaRepository.getModelList(modelMap))
                .isInstanceOf(TspException.class);
    }

    @Test
    @DisplayName("모델 상세 조회 테스트")
    void 모델상세조회테스트() {
        // given
        FrontModelEntity menFrontModelEntity = FrontModelEntity.builder().idx(156).build();

        FrontModelDTO menModelDTO = frontModelJpaRepository.getModelInfo(menFrontModelEntity);
        assertThat(menModelDTO.getIdx()).isEqualTo(156);
        assertThat(menModelDTO.getModelKorFirstName()).isEqualTo("주");
        assertThat(menModelDTO.getModelKorSecondName()).isEqualTo("선우");
        assertThat(menModelDTO.getModelFirstName()).isEqualTo("Joo");
        assertThat(menModelDTO.getModelSecondName()).isEqualTo("seon woo");

        FrontModelEntity womenFrontModelEntity = FrontModelEntity.builder().idx(143).build();

        FrontModelDTO womenModelDTO = frontModelJpaRepository.getModelInfo(womenFrontModelEntity);

        // then
        assertThat(womenModelDTO.getIdx()).isEqualTo(143);
        assertThat(womenModelDTO.getModelKorFirstName()).isEqualTo("김");
        assertThat(womenModelDTO.getModelKorSecondName()).isEqualTo("예영");
        assertThat(womenModelDTO.getModelFirstName()).isEqualTo("kim");
        assertThat(womenModelDTO.getModelSecondName()).isEqualTo("ye yeong");
    }

    @Test
    @DisplayName("모델 BDD 조회 테스트")
    void 모델BDD조회테스트() {
        // 정상
        // given
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("categoryCd", 1);
        modelMap.put("jpaStartPage", 1);
        modelMap.put("size", 3);

        List<CommonImageDTO> commonImageDtoList = new ArrayList<>();
        commonImageDtoList.add(commonImageDTO);

        List<FrontModelDTO> modelList = new ArrayList<>();
        modelList.add(FrontModelDTO.builder().idx(3).categoryCd(1).modelKorName("조찬희").modelImage(commonImageDtoList).build());

        // when
//        given(mockFrontModelJpaRepository.getModelList(modelMap)).willReturn(modelList);
        when(mockFrontModelJpaRepository.getModelList(modelMap)).thenReturn(modelList);

        // then
        assertThat(mockFrontModelJpaRepository.getModelList(modelMap).get(0).getIdx()).isEqualTo(modelList.get(0).getIdx());
        assertThat(mockFrontModelJpaRepository.getModelList(modelMap).get(0).getCategoryCd()).isEqualTo(modelList.get(0).getCategoryCd());
        assertThat(mockFrontModelJpaRepository.getModelList(modelMap).get(0).getModelKorName()).isEqualTo(modelList.get(0).getModelKorName());
        assertThat(mockFrontModelJpaRepository.getModelList(modelMap).get(0).getModelImage().get(0).getFileName()).isEqualTo(modelList.get(0).getModelImage().get(0).getFileName());

        // verify
        verify(mockFrontModelJpaRepository, times(4)).getModelList(modelMap);
        verify(mockFrontModelJpaRepository, atLeastOnce()).getModelList(modelMap);
        verifyNoMoreInteractions(mockFrontModelJpaRepository);
    }

    @Test
    @DisplayName("모델 상세 BDD 조회 테스트")
    void 모델상세BDD조회테스트() {
        // given
        commonImageEntityList.add(commonImageEntity);

        frontModelEntity = builder().idx(1).commonImageEntityList(commonImageEntityList).build();

        frontModelDTO = FrontModelDTO.builder()
                .idx(1)
                .categoryCd(1)
                .categoryAge("2")
                .modelKorName("조찬희")
                .modelEngName("CHOCHANHEE")
                .modelDescription("chaneeCho")
                .height("170")
                .size3("34-24-34")
                .shoes("270")
                .visible("Y")
                .modelImage(ModelImageMapper.INSTANCE.toDtoList(commonImageEntityList))
                .build();

        // when
//        given(mockFrontModelJpaRepository.getModelInfo(frontModelEntity)).willReturn(frontModelDTO);
        when(mockFrontModelJpaRepository.getModelInfo(frontModelEntity)).thenReturn(frontModelDTO);

        // then
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getIdx()).isEqualTo(1);
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getCategoryCd()).isEqualTo(1);
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getCategoryAge()).isEqualTo("2");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getModelKorName()).isEqualTo("조찬희");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getModelEngName()).isEqualTo("CHOCHANHEE");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getModelDescription()).isEqualTo("chaneeCho");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getHeight()).isEqualTo("170");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getSize3()).isEqualTo("34-24-34");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getShoes()).isEqualTo("270");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getVisible()).isEqualTo("Y");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getModelImage().get(0).getFileName()).isEqualTo("test.jpg");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getModelImage().get(0).getFileMask()).isEqualTo("test.jpg");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getModelImage().get(0).getFilePath()).isEqualTo("/test/test.jpg");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getModelImage().get(0).getImageType()).isEqualTo("main");
        assertThat(mockFrontModelJpaRepository.getModelInfo(frontModelEntity).getModelImage().get(0).getTypeName()).isEqualTo("model");

        // verify
        verify(mockFrontModelJpaRepository, times(15)).getModelInfo(frontModelEntity);
        verify(mockFrontModelJpaRepository, atLeastOnce()).getModelInfo(frontModelEntity);
        verifyNoMoreInteractions(mockFrontModelJpaRepository);
    }

    @Test
    @DisplayName("모델 메인 배너 조회 테스트")
    void 모델메인배너리스트조회테스트() {
        // when
        List<FrontModelDTO> mainModelList = frontModelJpaRepository.getMainModelList();

        Optional<FrontModelDTO> mainModelFirstInfo = frontModelJpaRepository.getMainModelList().stream().findFirst();

        // then
        assertThat(mainModelList).isNotEmpty();
        assertThat(mainModelFirstInfo.get().getCategoryCd()).isEqualTo(1);
        assertThat(mainModelFirstInfo.get().getModelMainYn()).isEqualTo("Y");
    }
}