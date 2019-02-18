package com.laoxu.tools;

import org.apache.commons.lang3.RandomStringUtils;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.util.Properties;
/**
 * 修改人：xdc
 * 创建时间：2019-02-15 17:40
 */
public class emailUtils {
    //用户名
    private static String username ;

    //授权码
    private static String password ;

    //是谁发过来的
    private static String from ;

    //  #跳转地址
    private static String activeUrl ;

    //发送内容
    private static String content ;

    //邮箱的标题
    private static String subject;

    //#host地址（163）
    private static String smtp_host ;

    /**
     *
     * @param username 用户名
     * @param password 邮箱授权码
     * @param from     发送的邮箱地址（企业）
     * @param smtp_host host地址 163（smtp.163.com）
     * @param activeUrl 跳转地址 https://xx/ 必须要加入http 或者https不然url地址失效
     * @param content  邮件内容
     * @param subject 邮箱标题
     */
    public emailUtils (String username, String password , String from,String smtp_host,
                       String activeUrl,String content,String subject) {
        this.username = username;
        this.password = password;
        this.from = from;
        this.smtp_host = smtp_host;
        this.activeUrl = activeUrl;
        this.content = content;
        this.subject = subject;
    }
    public emailUtils (){ }

    /**
     *
     * @param activecode 验证码
     * @param to       发送到的邮箱地址
     */
    public static void sendMail( String activecode, String to) {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtp_host);
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.auth", "true");
        Session session = Session.getInstance(props);
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipient(RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            String jump = "<br/><a href='"
                    + activeUrl + "?activecode="+activecode+"&email='"+to+">跳转地址"+activecode+"</a><br/>";
            message.setContent(content + jump, "text/html;charset=utf-8");
            Transport transport = session.getTransport();
            transport.connect(smtp_host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("邮件发送失败...");
        }
    }
    public static void main(String[] args) {
        emailUtils e = new emailUtils("xdcloas113@163.com","yqy123456","xdcloas113@163.com",
                "smtp.163.com","https://www.baidu.com/", "fack","测试跳转问题");
        e.sendMail("abc","235783655@qq.com");
    }

}
