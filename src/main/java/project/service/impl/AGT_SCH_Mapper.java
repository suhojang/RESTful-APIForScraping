package project.service.impl;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * 
 * 스케쥴정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Mapper("AGT_SCH_Mapper")
public interface AGT_SCH_Mapper {

	/**
	 * 스케쥴정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_SCH_S1000J(Map<String,Object> param) throws Exception;

	/**
	 * 스케쥴정보조회 - 스케쥴 Id로 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> AGT_SCH_V1000J(Map<String, Object> param) throws Exception;

	/**
	 * 스케쥴정보 등록
	 * @param param
	 * @throws Exception
	 */
	void AGT_SCH_I1000J(Map<String, Object> param) throws Exception;

	/**
	 * 스케쥴정보 수정
	 * @param param
	 * @throws Exception
	 */
	void AGT_SCH_U1000J(Map<String, Object> param) throws Exception;

	/**
	 * 스케쥴정보 삭제
	 * @param param
	 * @throws Exception
	 */
	void AGT_SCH_D1000J(Map<String, Object> param) throws Exception;

	/**
	 * Task할당 스케쥴 정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> AGT_SCH_S3000J(Map<String, Object> param) throws Exception;
	
	
}
