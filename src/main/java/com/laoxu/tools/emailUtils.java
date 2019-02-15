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
    //当前账户后期写入配置
    private static String password ;

    //mail.from=xdcloas113@163.com
    private static String from ;

    //    #跳转地址 后期写入配置
    //    mail.activeUrl=https://www.baidu.com/
    public static String activeUrl ;

    //发送内容
    public static String content ;

    //邮箱的标题
    public static String subject;

    //#host地址
    //mail.smtp_host=smtp.163.com
    private static String smtp_host ;

    {

    }
    public  void setUsername(String username) {
        this.username = username;
    }
    public  void setPassword(String password) {
        this.password = password;
    }
    public  void setFrom(String from) {
        this.from = from;
    }
    public  void setActiveUrl(String activeUrl) {
        this.activeUrl = activeUrl;
    }
    //邮箱内容
    public  void setContent(String content) {
        this.content = content;
    }
    //标题
    public  void setSubject(String subject) {
        this.subject = subject;
    }
    //host
    public  void setSmtp_host(String smtp_host) {
        this.smtp_host = smtp_host;
    }

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

//		sendMail("测试邮件", "你好??", "1004655482@qq.com");
    }

}
