package add.bus_annomaps;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.web.bind.annotation.RequestParam;

public interface AnnoMaps {
	/****************** api 터미널 ******************/
	   @Select("SELECT * FROM BUS_DO ORDER BY DO_CODE")
	   public ArrayList<HashMap<String, String>> getApiDo();

	   @Select("SELECT * FROM BUS_TERMINAL WHERE DO_CODE=#{do_code}")
	   public ArrayList<HashMap<String, String>> getApoSi(String do_code);

	   /****************** api 조회시 디비 인서트 ******************/
	   
	   // 경로코드 함수
	   @Select("SELECT BUS_ROUTE_INSERT(#{depPlaceNm}, #{arrPlaceNm}) TER_CODE FROM DUAL")
	   public HashMap<String, String> getTerCode(@Param("depPlaceNm") String depPlaceNm,@Param("arrPlaceNm") String arrPlaceNm);
	   
	   // 스케쥴 인서트
	   @Insert("{ CALL BUS_INSERT(#{grade, mode=IN, jdbcType=VARCHAR}, #{company},"
	         + " #{routecode}, #{day}, #{depPlandTime, mode=IN, jdbcType=VARCHAR},"
	         + " #{arrPlandTime, mode=IN, jdbcType=VARCHAR}, #{charge, mode=IN, jdbcType=VARCHAR})}")
	   @Options(statementType = StatementType.CALLABLE)
	   public void insertSchedule(@RequestParam HashMap<String, String> m);

	   // 디비에 검색한 값이 저장이 되어있는 지 체크
	   @Select("SELECT COUNT(*) FROM V_BUS WHERE SCHE_DAY = #{day} "
	         + "AND ROUTE_CODE = #{routecode} ORDER BY SCHE_CODE ASC")
	   public int dbCheck(@Param("day") String day, @Param("routecode") String routecode);
	   
	   // 화면에 뿌려줄 검색한 버스 스케줄 목록
	   @Select("SELECT * FROM V_BUS WHERE SCHE_DAY = #{day} AND ROUTE_CODE = #{routecode} ORDER BY SCHE_CODE ASC")
	   public ArrayList<HashMap<String, String>> getScheduel(@Param("day") String day, @Param("routecode") String routecode);
}