package control;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.BoardBean;
import model.BoardDAO;



@WebServlet("/BoardListCon.do")
public class BoardListCon extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		reqPro(request, response);
	/*	System.out.println("하이");
		String indexNum = request.getParameter("num");
		if(indexNum!=null) {
			int num = Integer.parseInt(indexNum);
			BoardDAO dao = BoardDAO.getInstance();
			BoardBean dto = dao.getBoardContent(num);
			
			request.setAttribute("dto", dto);
			System.out.println(dto.getContent());
			request.setAttribute("dto", dto);
			response.sendRedirect("a.jsp");
		}
		*/
	}
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		reqPro(request, response);
	}
	
	protected void reqPro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");// 인코딩 
		
		//BoardBean bean = new BoardBean();//??????????????
		
		//화면에 보여질 게시글의 개수를 지정
		int pageSize=10;
		//현재 보여지고 있는 페이지의 넘버값을 읽어드림
		String pageNum = request.getParameter("pageNum");
		//null처리
		if(pageNum ==  null) {
			pageNum="1";
		}
		//전체 게시글의 갯수
		int count = 0;
		//jsp페이지 내에서 보여질 넘버링 숫자값을 저장하는 변수 
		int number = 0;
		
		//현재 보여지고 있는 페이지 문자를  순자로 형변환
		int currentPage = Integer.parseInt(pageNum);
		//전체 게시글의 갯수를 가져와야 하기에 데이터 베이스 객체 생성
		BoardDAO bdao = new BoardDAO();
		count = bdao.getAllCount();
		
		//현재 보여질 페이지 시작 번호를 설정
		int startRow = (currentPage - 1)*pageSize+1;
		int endRow = currentPage * pageSize;
		
		//최신글 10개를 기준으로 개시글을 리턴 받아주는 메소드 호출
		Vector<BoardBean> v = bdao.getAllBoard(startRow, endRow);
		for(int i = 0; i<v.size(); i++)
			System.out.println(v.get(i).toString());
		
		number = count - (currentPage - 1) * pageSize;
		
		///////////////////////수정,삭제시 비밀번호가 틀렸다면 
		String msg = (String) request.getAttribute("msg");
		
		
		
		
		/////////////////////////////////////////////////BoardList.jsp쪽으로 request객체에 담아서 넘겨줌
		request.setAttribute("v", v);
		request.setAttribute("number", number);
		request.setAttribute("pageSize", pageSize);
		request.setAttribute("count", count);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("msg", msg);
	
		RequestDispatcher dis = request.getRequestDispatcher("BoardList.jsp");
		dis.forward(request, response);
		//RequestDispatcher = 현재 request에 담고 있는 정보를 저장하고 있다가 그 다음페이지, 다음페이지에서도
		//해당 정보를 볼 수 있게 저장하는 기능을한다.
		//forward( ) 메소드는 클라이언트의 요청으로 생성되는 HttpServletRequest와 HttpServletResponse 
		//객체를 다른 자원에 전달하고 수행 제어를 완전히 넘겨서 다른 자원의 수행 결과를 클라이언트로 응답하도록 하는 기능의 메소드이다.

	}

}
