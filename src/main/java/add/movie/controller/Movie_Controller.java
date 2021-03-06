package add.movie.controller;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import add.maps.TestMappable;
import add.movie.Mongo;
import add.movie.MovieJSONMain;
import add.movie.NaverApi;
import add.movie.Paging;
import add.movie.maps.MovieMappable;

@org.springframework.stereotype.Controller
@RequestMapping("/movie")
public class Movie_Controller {
	@Autowired
	Connection con;
	
	@Autowired
	NaverApi na;
	
	@Autowired
	MovieJSONMain mj;
	
	@Autowired
	MovieMappable mm;
	
	@Autowired
	Paging p;
	
	@Autowired
	Mongo mo;
	
	@Autowired
	TestMappable tm;
	
	// 메인
	@RequestMapping(value= {"/main"}, method=RequestMethod.GET)
	public ModelAndView Movie_main() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("rating", mo.getRating());
		mv.addObject("cinema", mo.getCinema());
		mv.addObject("genre", mo.getGenre());
		mv.addObject("movie", mm.boxList());
		mv.setViewName("movie_main");

		return mv;
	}
	
	// 상세 정보
	@RequestMapping(value= {"/detail_view"}, method=RequestMethod.GET)
	public ModelAndView detail_view(@RequestParam HashMap<String, String> m){
		ModelAndView mv = new ModelAndView();
		String title = mm.mv_dt_sel(m).get("MV_TITLE_KR");
		//오늘날짜 구하기
		Calendar c = Calendar.getInstance();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		String today = fmt.format(c.getTime());
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("mv_title_kr", title);
		hm.put("today", today);		
		mv.addObject("c_list", mm.tc(hm));
		mv.addObject("age", mo.getAge(title));
		mv.addObject("gender", mo.getGenderMV(title));
		mv.addObject("movie", mm.mv_dt_sel(m));
		mv.setViewName("detail_view");		
		return mv;
	}
	
	// 영화 정보
	@RequestMapping(value= {"/movie_list/{pnum}"}, method=RequestMethod.GET)
	public ModelAndView movie_list(@PathVariable(value="pnum") Integer pnum, HttpServletRequest rq) {
		ModelAndView mv = new ModelAndView();
		p.moviePaging(pnum, rq);
		mv.addObject("list", mm.movie_list(pnum));
		mv.setViewName("movie_list");
		
		return mv;
	}
	
	// 관리자 전체 예약 내역
	@RequestMapping(value= {"/rev_l_all/{pnum}"}, method=RequestMethod.GET)
	public ModelAndView re_list_all(@PathVariable(value="pnum") Integer pnum, HttpServletRequest rq) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		ModelAndView mv = new ModelAndView();
		HttpSession s = rq.getSession();
		String user = (String) s.getAttribute("LoginUser");
		
		if(user.equals("admin")) {
			p.reAllPaging(pnum, rq);
			list = mm.rev_l_all(pnum);
		}
		
		mv.addObject("rev_l", list);
		mv.setViewName("re_list_all");
		
		return mv;
	}
	
	// 회원 리스트
	@RequestMapping(value = {"/m_list/{pnum}"}, method=RequestMethod.GET)
	public ModelAndView m_list(@PathVariable(value="pnum") Integer pnum,HttpServletRequest rq) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		ModelAndView mv = new ModelAndView();
		
		p.mPaging(pnum, rq);
		list = mm.m_list();
		
		mv.addObject("m_list", list);
		mv.setViewName("m_list");
		
		return mv;
	}
	
	
	// 관리자용 예약 내역
	@RequestMapping(value= {"/rev_l_s/{pnum}"}, method=RequestMethod.POST)
	public ModelAndView re_list_s(@PathVariable(value="pnum") Integer pnum,@RequestParam HashMap<String, String> map, HttpServletRequest rq) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		ModelAndView mv = new ModelAndView();
		HttpSession s = rq.getSession();
		String user = map.get("m_id");
		String m_num = mm.userId(user);
		
		p.rePaging(m_num, pnum, rq);
		list = mm.rev_l(user, pnum);
		
		mv.addObject("m_list", map);
		mv.addObject("rev_l", list);
		mv.setViewName("re_list");
		
		return mv;
	}
	
	// 예약 내역
	@RequestMapping(value= {"/rev_l/{pnum}"}, method=RequestMethod.GET)
	public ModelAndView re_list(@PathVariable(value="pnum") Integer pnum, HttpServletRequest rq) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		ModelAndView mv = new ModelAndView();
		HttpSession s = rq.getSession();
		String user = (String) s.getAttribute("LoginUser");
		String m_num = mm.userId(user);
		
		p.rePaging(m_num, pnum, rq);
		list = mm.rev_l(user, pnum);
		
		mv.addObject("rev_l", list);
		mv.setViewName("re_list");
		
		return mv;
	}
	
	//예약 페이지
	@RequestMapping(value= {"/reservation"}, method=RequestMethod.GET)
	public ModelAndView reservation() {
		ModelAndView mv = new ModelAndView();
		Calendar c = Calendar.getInstance();
		//오늘 날짜 구하기
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		String today = fmt.format(c.getTime());
		//요일 구하기
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		Date currentTime = new Date();
		ArrayList<HashMap<String, String>> daylist = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> daydate = null;
		String day = null;
		String dates = null;
		for (int i = 0; i < 7; i++) {
			daydate = new HashMap<String, String>();
			c.setTime(currentTime);
			c.add(Calendar.DATE, i);
			dates = formatter.format(c.getTime());
			dates = dates.substring(8);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			switch (dayOfWeek) {
			case 1:
				day = "일";
				break;
			case 2:
				day = "월";
				break;
			case 3:
				day = "화";
				break;
			case 4:
				day = "수";
				break;
			case 5:
				day = "목";
				break;
			case 6:
				day = "금";
				break;
			case 7:
				day = "토";
				break;
			}
			daydate.put("dates", dates);
			daydate.put("days", day);
			daylist.add(daydate);
	    }
	    mv.addObject("mv_list",mm.mv_select(today));
	    mv.addObject("daylist", daylist);
	    mv.setViewName("reservation");
		return mv;
	}
	
	//좌석선택
	@RequestMapping(value= {"/reservation_seat"}, method=RequestMethod.GET)
	public ModelAndView getSeat(@RequestParam HashMap<String, String> map) {
		ModelAndView mv = new ModelAndView();
		String sd_code = mm.SDC(map);
		map.put("sd_code", sd_code);
		
		mv.addObject("mv_list",map);
		mv.addObject("sd_list", mm.sd(sd_code));
		mv.addObject("seat", mm.seat(sd_code));
		mv.addObject("seat_count", mm.sc(sd_code));
		mv.setViewName("reservation_seat");
		
		return mv;
	}
	
	// 관리자 등록
	@RequestMapping(value= {"/registration"}, method=RequestMethod.GET)
	public ModelAndView getRegist() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("movielist", mm.boxList());
		mav.setViewName("registration");
		return mav;
	}
	
	// 영화 등록
   @RequestMapping(value= {"/mv_insert"}, method=RequestMethod.GET)
   public String mv_insert() throws Exception {
      ArrayList<HashMap<String, String>> list = mj.Mm();
      HashMap<String, Object> m = null;
      HashMap<String, String> dtm = null;
      StringTokenizer tokens = null;
      mm.mv_update();
      for(HashMap<String,String> hm : list) {
         dtm = new HashMap<String, String>();
         // 순위
         String rnum = hm.get("rnum");
         dtm.put("rnum", rnum);
         // 포스터
         tokens = new StringTokenizer(hm.get("posters"));
         String poster = tokens.nextToken("|");
         dtm.put("poster", poster);
         // 줄거리
         String story = hm.get("plot");
         dtm.put("story", story);
         /* 영화 상세 정보 */
         m = (HashMap<String, Object>) mj.MV(hm.get("movieCd")).get("movie_view");
         // 제목(한)
         String title_kr = (String) m.get("movieNm");
         dtm.put("title_kr", title_kr);
         // 제목(영)
         String title_en = (String) m.get("movieNmEn");
         if(title_en.equals("")) {
        	 title_en = " ";   	 
         } 
    	 dtm.put("title_en", title_en);
         // 국가
         ArrayList<HashMap<String, String>> country = 
        		 (ArrayList<HashMap<String, String>>) m.get("nations");
         for(HashMap<String, String> c : country) {
            String ct = c.get("nationNm");
            dtm.put("ct", ct);
         }
         // 개봉일
         String odt = (String) m.get("openDt");
         dtm.put("odt", odt);
         // 상영 시간
         String st = (String) m.get("showTm");
         dtm.put("st", st);
         // 상영 등급
         ArrayList<HashMap<String, String>> audit = 
        		 (ArrayList<HashMap<String, String>>) m.get("audits");
         String sg = audit.get(0).get("watchGradeNm");
         dtm.put("sg", sg);
         // 평점
         String rating = na.NaverApi(title_kr).get("userRating");
         dtm.put("rating", rating);
         //영화 코드
         int mv_c = Integer.parseInt(mm.mv_insert(dtm));
         // 감독
         ArrayList<HashMap<String, String>> director = 
        		 (ArrayList<HashMap<String, String>>) m.get("directors");
         for(HashMap<String, String> d : director) {
            String dt = d.get("peopleNm");
            mm.dt_insert(mv_c, dt);
         }
         // 배우
         ArrayList<HashMap<String, String>> actor = 
        		 (ArrayList<HashMap<String, String>>) m.get("actors");
    	 if(actor.isEmpty()) {
    		 String ac = na.NaverApi(title_kr).get("actor");
    		 System.out.println(ac);
    		 String date[] = ac.split("\\|");    	        
	        for(int i=0 ; i<date.length ; i++) {
	        	String at = date[i];
	        	mm.at_insert(mv_c, at);
	        }
    	 } else {
	         for(HashMap<String, String> a : actor) {
	            String at = a.get("peopleNm");
	            mm.at_insert(mv_c, at);	         
	    	 }
    	 }
    	 //장르
         ArrayList<HashMap<String, String>> genre = 
        		 (ArrayList<HashMap<String, String>>) m.get("genres");
         for(HashMap<String, String> g : genre) {
            String gr = g.get("genreNm");
            mm.gr_insert(mv_c, gr);
            //System.out.println("장르 : " + gr);
         }
      }
      	return "redirect:/movie/main";
   }
   
   // 상영 영화 등록
   @RequestMapping(value = {"/regi_ok"}, method = RequestMethod.POST)
   public String mv_in(@RequestParam HashMap<String, String> map) {
	   //두 날짜 사이 값 구하기
	   String d1 = map.get("odt");
	   String d2 = map.get("cdt");      
 	   
      
 	   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");      
 	   try {
 		   Date date = sdf.parse(d1);
 		   Date date2 = sdf.parse(d2);
         
 		   ArrayList<String> dates = new ArrayList<String>();
 		   Date startDate = date;
 		   while( startDate.compareTo( date2 ) <=0 ){
 			   dates.add(sdf.format(startDate));
 			   Calendar c = Calendar.getInstance();
 			   c.setTime(startDate);
 			   c.add(Calendar.DAY_OF_MONTH, 1);
 			   startDate = c.getTime();
 		   } 		  
 		   for(String s2 : dates) {  	
 			   map.put("sd_day", s2); 			   
 			   String run = map.get("in_time");
 		 	   String start = map.get("f_time");
 		 	   String end = map.get("e_time"); 		       
 		 	   //상영시작시간부터 끝시간까지 상영간격에 따라 INSERT
 		 	   int runtime = Integer.parseInt(run);
 		 	   int st_time = Integer.parseInt(start);
 		 	   int en_time = Integer.parseInt(end);
 			   for(int i = st_time; i <= en_time;i+=runtime) {
 				   int hour =i/60;
	      		   int min = ((i*60)%3600)/60;
	      		   String h = Integer.toString(hour);
	      		   StringBuffer sb2 = new StringBuffer(h);
	      		   String m = Integer.toString(min);
	      		   StringBuffer sb = new StringBuffer(m);
	      		   if (hour < 10) {
	      			   sb2.insert(0, 0);
	      		   }
	      		   if (min < 10) {
	      			   sb.insert(0, 0);
	      		   }
	      		   String times = sb2.toString() + sb.toString();
	
	      		   map.put("t_time", times);			
	      		   //System.out.println("map" + map);
	      		   mm.mv_in(map);
	      		   
	      		}
 		   }
		} catch (ParseException e) {
	        e.printStackTrace();
		}
 	   
		return "redirect:/movie/main";
	}
   
   // 결제
   @RequestMapping(value = {"/pay"}, method = RequestMethod.POST)
   public ModelAndView pay(@RequestParam HashMap<String, String> map) {
	   ModelAndView mv =new ModelAndView();
	   ArrayList<HashMap<String, String>> alhm = new ArrayList<HashMap<String, String>>();
	   HashMap<String, String> hm = null;
	   for(char ch = 'A';ch < 'G'; ch++) {
		   for(int i=1;i<10;i++) {
			   String a = Integer.toString(i);
			   String s_name = ch+a;
			   if(s_name.equals(map.get("s_name"+s_name))) {
				  hm = new HashMap<String, String>();
				  hm.put("s_name", s_name);
				  alhm.add(hm);
			   }
		   }
	   }
	   mv.addObject("s_names", alhm);
	   mv.addObject("ms_list", map);
	   mv.addObject("mv_info", mm.sd(map.get("sd_code")));
	   mv.setViewName("pay");
	   return mv;
   }
   
   // 결제 완료
   @RequestMapping(value = {"/ticket"}, method = RequestMethod.POST)
   public ModelAndView ticket(@RequestParam HashMap<String, String> map, HttpSession s) {
	   ModelAndView mv = new ModelAndView();
	   ArrayList<HashMap<String, String>> alhm = new ArrayList<HashMap<String, String>>();
	   HashMap<String, String> hm = null;
	   String user = (String) s.getAttribute("LoginUser");
	   //예매 좌석 비교 후 좌석 값 ArrayList<HashMap<String, String>>에 저장
	   for(char ch = 'A';ch < 'G'; ch++) {
		   for(int i=1; i < 10; i++) {
			   String a = Integer.toString(i);
			   String s_name = ch+a;
			   if(s_name.equals(map.get("s_name"+s_name))) {
				   hm = new HashMap<String, String>();
				   hm.put("u_id", user);
				   hm.put("sd_code", map.get("sd_code"));
				   hm.put("s_name", s_name);
				   hm.put("r_price", map.get("r_price"));
				   alhm.add(hm);
			   }
		   }
	   }
	   //예매된 좌석 정보 DB에 저장
	   for(HashMap<String, String> m : alhm ) {
		   mm.re(m);
	   }
	   
	   HashMap<String, String> mv_s = mm.sd(map.get("sd_code"));
	   String m_num = (String) s.getAttribute("LoginNum");
	   String gender = (String) s.getAttribute("LoginGender");
	   
	   if(gender == null) {
		   gender="0";
	   }	
	   
	   mv_s.put("M_GENDER", gender);
	   mv_s.put("m_num", m_num);
	   mo.insertCinema(mv_s);
	   String genre = mv_s.get("G_NAME");
	   
	   if(genre.contains(",")) {
		   String[] genres = genre.split(",");
		   for(String g : genres) {					
			   mo.insertGenre(mv_s, g);
		   }				
	   } else {
		   mo.insertGenre(mv_s, genre);
	   }
	   
	   // 나이 계산
	   Calendar cal = Calendar.getInstance();
	   String birth = mm.age(user);
	   
	   // 현재 년도
	   int to_year = cal.get(cal.YEAR);
	   
	   int year = Integer.parseInt(birth.substring(0, 4));
	   int age = (to_year - year);
	   
	   int ag = (int) Math.floor(age/10);
	   String mv_title_kr = mv_s.get("MV_TITLE_KR");
	   
	   int gd = Integer.parseInt(gender);
	   
	   mo.insertGenderMV(mv_title_kr, gd);
	   mo.insertAge(mv_title_kr, ag);
	   mo.insertRating(mv_s);
	   mv.addObject("seat_num", alhm);
	   mv.addObject("mv_info", mv_s);
	   mv.addObject("r_list", map); 
	   mv.setViewName("ticket");
	   return mv;
   }
   
   // 예약 취소
   @RequestMapping(value= {"/del"}, method = RequestMethod.POST)
   public String r_del(@RequestParam String r_code) {
	   mm.r_del(r_code);
	   return "redirect:/movie/rev_l/1";
   }
   
   // 좋아요 검색
   
}