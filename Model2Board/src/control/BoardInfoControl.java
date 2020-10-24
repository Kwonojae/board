package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.BoardBean;
import model.BoardDAO;


@WebServlet("/BoardInfoControl.do")
public class BoardInfoControl extends HttpServlet {



	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		reqPro(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		reqPro(request, response);
	}

	protected void reqPro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//num 값을 리턴받는곳 
		int seq = Integer.parseInt(request.getParameter("seq"));
		//데이터 베이스에 접근
		BoardDAO bdao = new BoardDAO();
		//하나의 게시글에 대한 정보를 리턴 
		BoardBean bean = bdao.getOneBoard(seq);
		
		request.setAttribute("bean", bean);
		
		//view -jsp쪽으로 데이터를 넘겨준다 
		RequestDispatcher dis = request.getRequestDispatcher("BoardInfo.jsp");
		dis.forward(request, response);
	}
}
