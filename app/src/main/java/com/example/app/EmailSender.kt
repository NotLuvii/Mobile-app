package com.example.app

//import java.util.Properties
//import javax.mail.Message
//import javax.mail.MessagingException
//import javax.mail.PasswordAuthentication
//import javax.mail.Session
//import javax.mail.Transport
//import javax.mail.internet.InternetAddress
//import javax.mail.internet.MimeMessage

object EmailSender {

    fun send() {
//        Thread {
//            val username = "yourname@yourcompany.com"
//            val password = "yourpassword"
//
//            val props = Properties()
//            props["mail.smtp.auth"] = "true"
//            props["mail.smtp.starttls.enable"] = "true"
//            props["mail.smtp.host"] = "smtp.example.com"
//            props["mail.smtp.port"] = "587"
//
//            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
//                override fun getPasswordAuthentication(): PasswordAuthentication {
//                    return PasswordAuthentication(username, password)
//                }
//            })
//
//            try {
//                val message = MimeMessage(session)
//                message.setFrom(InternetAddress("to@yourdomain.com"))
//                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("to@yourdomain.com"))
//                message.subject = "Testing Subject"
//                message.setText("Dear Dude," + "\n\n I came from Android SMTP!")
//
//                Transport.send(message)
//
//                println("Done")
//
//            } catch (e: MessagingException) {
//                throw RuntimeException(e)
//            }
//        }.start()
    }
}
