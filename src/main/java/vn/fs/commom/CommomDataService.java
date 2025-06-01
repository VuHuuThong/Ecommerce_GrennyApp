package vn.fs.commom;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import vn.fs.dto.Mailsup;
import vn.fs.entities.CartItem;
import vn.fs.entities.Order;
import vn.fs.entities.User;
import vn.fs.repository.FavoriteRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.service.ShoppingCartService;
@Service
public class CommomDataService {

	@Autowired
	FavoriteRepository favoriteRepository;  // Được tiêm tự động (DI) để sử dụng repository xử lý dữ liệu yêu thích (Favorite)

	@Autowired
	ShoppingCartService shoppingCartService;  // Được tiêm tự động (DI) để sử dụng dịch vụ giỏ hàng (ShoppingCart)

	@Autowired
	ProductRepository productRepository;  // Được tiêm tự động (DI) để sử dụng repository xử lý dữ liệu sản phẩm

	@Autowired
	public JavaMailSender emailSender;  // Được tiêm tự động (DI) để gửi email qua JavaMailSender

	@Autowired
	TemplateEngine templateEngine;  // Được tiêm tự động (DI) để sử dụng Thymeleaf tạo nội dung email từ template

	// Phương thức này để thêm dữ liệu chung vào model, dùng cho giao diện người dùng
	public void commonData(Model model, User user) {
		// Lấy thông tin sản phẩm theo danh mục để hiển thị lên giao diện
		listCategoryByProductName(model);

		Integer totalSave = 0;
		// Nếu người dùng đã đăng nhập, lấy số lượng sản phẩm yêu thích của người dùng đó
		if (user != null) {
			totalSave = favoriteRepository.selectCountSave(user.getUserId());
		}

		// Lấy số lượng mặt hàng trong giỏ hàng
		Integer totalCartItems = shoppingCartService.getCount();

		model.addAttribute("totalSave", totalSave);  // Thêm số lượng sản phẩm yêu thích vào model
		model.addAttribute("totalCartItems", totalCartItems);  // Thêm số lượng mặt hàng trong giỏ hàng vào model

		// Lấy tất cả mặt hàng trong giỏ hàng
		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cartItems);  // Thêm các mặt hàng giỏ hàng vào model

		// Tính tổng giá trị giỏ hàng
		double totalPrice = 0;
		for (CartItem cartItem : cartItems) {
			// Tính giá mỗi mặt hàng (giảm giá nếu có)
			double price = cartItem.getQuantity() * cartItem.getProduct().getPrice();
			totalPrice += price - (price * cartItem.getProduct().getDiscount() / 100);  // Cộng dồn vào tổng giá
		}

		model.addAttribute("totalPrice", totalPrice);  // Thêm tổng giá trị giỏ hàng vào model
	}

	// Phương thức này đếm số lượng sản phẩm theo danh mục
	public void listCategoryByProductName(Model model) {
		// Lấy dữ liệu thống kê sản phẩm theo danh mục từ database
		List<Object[]> coutnProductByCategory = productRepository.listCategoryByProductName();
		model.addAttribute("coutnProductByCategory", coutnProductByCategory);  // Thêm vào model để hiển thị trên giao diện
	}

	// Phương thức này gửi email thông báo khi đơn hàng thành công
	public void sendSimpleEmail(String email, String subject, String contentEmail, Collection<CartItem> cartItems,
								double totalPrice, Order orderFinal) throws MessagingException {
		// Lấy ngôn ngữ của người dùng (locale) để hỗ trợ nhiều ngôn ngữ trong email
		Locale locale = LocaleContextHolder.getLocale();

		// Chuẩn bị context cho Thymeleaf
		Context ctx = new Context(locale);
		ctx.setVariable("cartItems", cartItems);  // Thêm giỏ hàng vào context
		ctx.setVariable("totalPrice", totalPrice);  // Thêm tổng giá trị giỏ hàng vào context
		ctx.setVariable("orderFinal", orderFinal);  // Thêm thông tin đơn hàng vào context

		// Tạo một MimeMessage mới để gửi email
		MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
		mimeMessageHelper.setSubject(subject);  // Đặt tiêu đề cho email
		mimeMessageHelper.setTo(email);  // Đặt người nhận email

		// Sử dụng Thymeleaf để tạo nội dung email từ template
		String htmlContent = templateEngine.process("mail/email_en.html", ctx);  // Template HTML cho email
		mimeMessageHelper.setText(htmlContent, true);  // Đặt nội dung HTML cho email

		// Gửi email
		emailSender.send(mimeMessage);
	}

	// Phương thức này gửi email hỗ trợ tới người dùng
	public void sendEmailSupport(String email, String subject, Mailsup mailsup) throws MessagingException {
		// Lấy ngôn ngữ của người dùng (locale) để hỗ trợ nhiều ngôn ngữ trong email
		Locale locale = LocaleContextHolder.getLocale();

		// Chuẩn bị context cho Thymeleaf
		Context ctx = new Context(locale);
		ctx.setVariable("mailsup", mailsup);  // Thêm thông tin hỗ trợ vào context

		// Tạo một MimeMessage mới để gửi email
		MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
		mimeMessageHelper.setSubject(subject);  // Đặt tiêu đề cho email
		mimeMessageHelper.setTo(email);  // Đặt người nhận email

		// Sử dụng Thymeleaf để tạo nội dung email từ template
		String htmlContent = templateEngine.process("mail/email_sup.html", ctx);  // Template HTML cho email hỗ trợ
		mimeMessageHelper.setText(htmlContent, true);  // Đặt nội dung HTML cho email

		// Gửi email
		emailSender.send(mimeMessage);
	}
}