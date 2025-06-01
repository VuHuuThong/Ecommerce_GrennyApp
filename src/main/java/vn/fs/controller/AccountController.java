package vn.fs.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vn.fs.dto.ChangePassword;
import vn.fs.entities.User;
import vn.fs.repository.UserRepository;
import vn.fs.service.SendMailService;

/**
 * @author DongTHD
 *
 */
@Controller
public class AccountController {

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	HttpSession session;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SendMailService sendMailService;

	@GetMapping(value = "/forgotPassword")
	public String forgotPassword() {

		return "web/forgotPassword";
	}

	@PostMapping("/forgotPassword")  // Xử lý yêu cầu POST khi người dùng gửi yêu cầu quên mật khẩu (email)
	public ModelAndView forgotPassowrd(ModelMap model, @RequestParam("email") String email) {
		List<User> listUser = userRepository.findAll();  // Lấy tất cả người dùng từ cơ sở dữ liệu thông qua repository
		// Duyệt qua danh sách tất cả người dùng trong cơ sở dữ liệu để kiểm tra xem email người dùng nhập có tồn tại hay không
		for (User user : listUser) {
			if (email.trim().equals(user.getEmail())) {  // Kiểm tra nếu email người dùng nhập vào có trùng với email trong cơ sở dữ liệu
				session.removeAttribute("otp");  // Xóa OTP cũ khỏi session, đảm bảo không bị trùng OTP cũ khi người dùng yêu cầu lại
				// Tạo một mã OTP ngẫu nhiên gồm 6 chữ số (từ 100000 đến 999999)
				int random_otp = (int) Math.floor(Math.random() * (999999 - 100000 + 1) + 100000);
				session.setAttribute("otp", random_otp);  // Lưu mã OTP vào session để sử dụng sau này cho việc xác thực OTP

				// Tạo nội dung email HTML, chứa mã OTP
				String body = "<div>\r\n" + "<h3>Mã xác thực OTP của bạn là: <span style=\"color:#119744; font-weight: bold;\">"
						+ random_otp + "</span></h3>\r\n" + "</div>";

				// Gửi email với mã OTP cho người dùng thông qua dịch vụ sendMailService
				sendMailService.queue(email, "Quên mật khẩu?", body);

				model.addAttribute("email", email);  // Thêm email vào model để sử dụng trong view
				model.addAttribute("message", "Mã xác thực OTP đã được gửi tới Email : " + user.getEmail() + " , hãy kiểm tra Email của bạn!");  // Thêm thông báo vào model để hiển thị trên giao diện
				return new ModelAndView("/web/confirmOtpForgotPassword", model);  // Chuyển hướng người dùng đến trang xác nhận OTP
			}
		}
		model.addAttribute("error", "Email này chưa đăng ký!");  // Nếu không tìm thấy email trong cơ sở dữ liệu, thông báo lỗi
		return new ModelAndView("web/forgotPassword", model);  // Quay lại trang quên mật khẩu với thông báo lỗi
	}

	@PostMapping("/confirmOtpForgotPassword")
	public ModelAndView confirm(ModelMap model, @RequestParam("otp") String otp, @RequestParam("email") String email) {
		// Kiểm tra nếu mã OTP người dùng nhập vào khớp với OTP trong session
		if (otp.equals(String.valueOf(session.getAttribute("otp")))) {
			// Nếu OTP đúng, chuyển đến trang thay đổi mật khẩu
			model.addAttribute("email", email);  // Truyền email vào model để người dùng không phải nhập lại
			model.addAttribute("newPassword", "");  // Khởi tạo trường mật khẩu mới trong form
			model.addAttribute("confirmPassword", "");  // Khởi tạo trường xác nhận mật khẩu
			model.addAttribute("changePassword", new ChangePassword());  // Khởi tạo đối tượng ChangePassword để bind với form

			// Trả về trang thay đổi mật khẩu
			return new ModelAndView("web/changePassword", model);
		}

		// Nếu OTP không đúng, hiển thị thông báo lỗi và yêu cầu người dùng thử lại
		model.addAttribute("error", "Mã xác thực OTP không đúng, thử lại!");
		return new ModelAndView("web/confirmOtpForgotPassword", model);  // Quay lại trang xác nhận OTP
	}

	@PostMapping("/changePassword")
	public ModelAndView changeForm(ModelMap model,
								   @Valid @ModelAttribute("changePassword") ChangePassword changePassword, BindingResult result,
								   @RequestParam("email") String email, @RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword) {
		// Kiểm tra nếu có lỗi trong form (ví dụ: mật khẩu không hợp lệ hoặc không khớp)
		if (result.hasErrors()) {
			model.addAttribute("newPassword", newPassword);  // Giữ lại mật khẩu mới người dùng nhập vào
			model.addAttribute("newPassword", confirmPassword);  // Giữ lại mật khẩu xác nhận người dùng nhập vào
			model.addAttribute("email", email);  // Giữ lại email người dùng nhập vào
			return new ModelAndView("/web/changePassword", model);  // Trả lại trang thay đổi mật khẩu nếu có lỗi
		}

		// Kiểm tra nếu mật khẩu mới và mật khẩu xác nhận không khớp
		if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
			model.addAttribute("newPassword", newPassword);  // Giữ lại giá trị mật khẩu mới
			model.addAttribute("newPassword", confirmPassword);  // Giữ lại giá trị mật khẩu xác nhận
			model.addAttribute("error", "error");  // Thêm thông báo lỗi vào model
			model.addAttribute("email", email);  // Giữ lại email của người dùng
			return new ModelAndView("/web/changePassword", model);  // Quay lại trang thay đổi mật khẩu với lỗi
		}

		// Lấy thông tin người dùng từ cơ sở dữ liệu theo email
		User user = userRepository.findByEmail(email);
		user.setStatus(true);  // Đảm bảo người dùng vẫn có trạng thái hoạt động
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));  // Mã hóa mật khẩu mới và cập nhật vào đối tượng người dùng
		userRepository.save(user);  // Lưu đối tượng người dùng với mật khẩu mới vào cơ sở dữ liệu

		// Thêm thông báo thành công vào model
		model.addAttribute("message", "Đặt lại mật khẩu thành công!");
		model.addAttribute("email", "");  // Xóa email khỏi model để tránh hiển thị lại
		session.removeAttribute("otp");  // Xóa mã OTP khỏi session sau khi thay đổi mật khẩu thành công
		return new ModelAndView("/web/changePassword", model);  // Trả lại trang thay đổi mật khẩu với thông báo thành công
	}

}
