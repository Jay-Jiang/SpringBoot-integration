package com.demo.springboot.cache;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author - Jianghj
 * @since - 2020-04-06 16:40
 */
@SpringBootTest
public class SpringBootMailTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootMailTest.class);

    @Resource(mappedName = "mailSender")
    JavaMailSender mailSender;

    /**
     * 发送简单邮件，只有简单的文本信息
     */
    @Test
    public void testSendMail() {
        //创建简单邮件实例
        SimpleMailMessage mail = new SimpleMailMessage();
        //添加邮件标题
        mail.setSubject("通知-开会");
        //添加邮件内容
        mail.setText("今晚7点，全员开会，开会...");
        //添加收件人（可以单个或多个）
        mail.setTo("445667@163.com", "8891002@163.com");
        //添加发件人
        mail.setFrom("556677889@qq.com");
        try {
            //发送邮件
            mailSender.send(mail);
        } catch (MailException e) {
            LOGGER.error("邮件发送失败：[{}]", e.getMessage());
        }
    }

    /**
     * 测试发送复杂的邮件，如一个页面
     */
    @Test
    public void testSendHtmlMail(){
        //创建一个复杂的邮件消息实例
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            //通过 Helper 来为复杂的邮件实例，填充内容，multipart 表示邮件体中是否上传附件
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            //添加邮件标题
            messageHelper.setSubject("通知-开会");
            //设置邮件内容（第二个参数：true 表示内容为 html，false 表示为纯文本-不解析html标签）
            messageHelper.setText("<h2>今晚7点，全员开会,...</h2>",true);
            //添加收件人（可以单个或多个）
            messageHelper.setTo(new String[]{"445667@163.com", "8891002@163.com"});
            //添加发件人
            messageHelper.setFrom("556677889@qq.com");
            //添加附件（可添加多个附件）
            messageHelper.addAttachment("开会通知.pdf",new File("/开会通知.pdf"));
            //发送邮件
            mailSender.send(mimeMessage);

        } catch (MailException e) {
            LOGGER.error("邮件发送失败：[{}]", e.getMessage());
        } catch (MessagingException e) {
            LOGGER.error("邮件体封装失败：[{}]", e.getMessage());
        }

    }
}
