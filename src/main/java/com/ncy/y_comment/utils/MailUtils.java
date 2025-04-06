package com.ncy.y_comment.utils;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MailUtils {
    public static void main(String[] args) throws MessagingException {
        sendMail("calvinlismmm@gmail.com",new MailUtils().achieveCode());

    }
    public static void sendMail(String email, String code) throws MessagingException {

        System.out.println("code2"+code);


        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");

        // 端口设置，选择587用于TLS加密
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true"); // 启用 TLS
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // 设置你的 Gmail 地址和应用专用密码
        props.put("mail.user", "yueccc666888@gmail.com"); // 你的 Gmail 邮箱地址
        props.put("mail.password", "mvvocyspczegbiua");
        //SMTP server
        /*props.put("mail.smtp.host", "smtp.qq.com");
        //SMTP
        props.put("mail.smtp.auth", "true");
        //port
        *//*props.put("mail.smtp.port", "465");
        // 启用 TLS
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");*//*
        props.put("mail.smtp.port", "587");
// Enable STARTTLS
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.user","9586658116@qq.com");
        props.put("mail.password","ajkxgldorqxtbfbf");*/

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(username, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        MimeMessage message = new MimeMessage(session);
        InternetAddress from = new InternetAddress(props.getProperty("mail.user"));
        message.setFrom(from);

        /*InternetAddress to = new InternetAddress(props.getProperty(email));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));*/
        try {
            InternetAddress to = new InternetAddress(email, true);
            message.setRecipient(MimeMessage.RecipientType.TO, to);
        } catch (AddressException e) {
            throw new IllegalArgumentException("邮箱地址格式错误：" + email);
        }

        // ✅ 修正：直接写邮件标题
        message.setSubject("验证码-verification code");

        // ✅ 修正：邮件内容不会出错
        message.setContent("尊敬的用户:你好!\n注册验证码为: " + code + " (有效期为一分钟,请勿告知他人)", "text/html;charset=UTF-8");

        // 发送邮件
        Transport.send(message);
    }

    public static String achieveCode(){
        String[] beforeShuffle = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a",
                "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z"};
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuffer sb = new StringBuffer();

        for (String s : list) {
            //System.out.println(s);
            sb.append(s);
        }
        System.out.println(sb.toString());
        // ✅ 修正：确保不会出错
        if (sb.length() < 8) {
            return "ABCDE"; // 兜底
        }
        System.out.println(sb.substring(3,8));
        return sb.substring(3,8);
    }
}
