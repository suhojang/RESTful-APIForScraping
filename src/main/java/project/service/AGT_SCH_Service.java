package project.service;

import java.util.List;
import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * 스케쥴정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
public interface AGT_SCH_Service {

	/**
	 * 스케쥴정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_SCH_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 스케쥴정보 단일 조회
	 * @param param
	 * @throws Exception
	 */
	Map<String,Object> AGT_SCH_V1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * 스케쥴정보 등록
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_SCH_I1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * 스케쥴정보 수정
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_SCH_U1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * 스케쥴정보 삭제
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_SCH_D1000J(Map<String, Object> param) throws DefinedException;

	/**
	 * Task 할당 스케쥴정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_SCH_S3000J(Map<String,Object> param) throws Exception;	
}
