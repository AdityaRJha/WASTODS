package com.solevictus.adityaroshanjha.utils.enums;

public enum EmailTemplates {

    ACCOUNT_VERIFICATION_OTP {
        @Override
        public String getBody(String otp) {
            return baseTemplate(
                    "🔐 Verify Your Account",
                    "Use the OTP below to verify your account:",
                    otp,
                    "This OTP is valid for 24 hours."
            );
        }
    },

    VERIFICATION_SUCCESS {
        @Override
        public String getBody(String otp) {
            return "<!DOCTYPE html>" +
                    "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +

                    "<div style='max-width: 500px; margin: auto; background: white; padding: 20px; border-radius: 10px; text-align: center;'>" +

                    "<h2 style='color: #28a745;'>✅ Account Verified Successfully</h2>" +

                    "<p style='color: #555;'>Hola,</p>" +

                    "<p style='color: #555;'>Your account has been successfully verified.</p>" +

                    "<p style='color: #555;'>You can now log in and start using all the features of our platform.</p>" +

                    "<div style='margin: 20px 0;'>" +
                    "<a href='#' style='background:#2d89ef;color:white;padding:10px 20px;border-radius:5px;text-decoration:none;'>Login Now</a>" +
                    "</div>" +

                    "<hr style='margin: 20px 0;'/>" +

                    "<p style='font-size: 12px; color: #aaa;'>If this wasn't you, please contact support immediately.</p>" +

                    "<p style='color: #555;'>Best regards,<br/>The Team</p>" +

                    "</div>" +
                    "</body>" +
                    "</html>";
        }
    },

    RESET_PASSWORD_OTP {
        @Override
        public String getBody(String otp) {
            return baseTemplate(
                    "🔐 Password Reset Request",
                    "Use the OTP below to reset your password:",
                    otp,
                    "This OTP is valid for 5 minutes."
            );
        }
    },

    WELCOME {
        @Override
        public String getBody(String otp) {
            return "<!DOCTYPE html>" +
                    "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +

                    "<div style='max-width: 500px; margin: auto; background: white; padding: 20px; border-radius: 10px; text-align: center;'>" +

                    "<h2 style='color: #333;'>👋 Welcome to adityaroshanjha platform!</h2>" +

                    "<p style='color: #555;'>Hola,</p>" +

                    "<p style='color: #555;'>Thank you for registering with us! We're excited to have you on board.</p>" +

                    "<p style='color: #555;'>Complete your profile verification using the OTP sent to your email.</p>" +

                    "<hr style='margin: 20px 0;'/>" +

                    "<p style='font-size: 12px; color: #aaa;'>If you have any questions, contact support.</p>" +

                    "<p style='color: #555;'>Best regards,<br/>The Team</p>" +

                    "</div>" +
                    "</body>" +
                    "</html>";
        }
    };



    public abstract String getBody(String otp);

    // 🔥 Shared template (DRY)
    protected static String baseTemplate(String title, String message, String otp, String validity) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +

                "<div style='max-width: 500px; margin: auto; background: white; padding: 20px; border-radius: 10px; text-align: center;'>" +

                "<h2 style='color: #333;'>" + title + "</h2>" +

                "<p style='color: #555;'>Hola,</p>" +

                "<p style='color: #555;'>" + message + "</p>" +

                "<div style='font-size: 28px; font-weight: bold; color: #2d89ef; margin: 20px 0;'>" +
                otp +
                "</div>" +

                "<p style='color: #777;'>" + validity + "</p>" +

                "<hr style='margin: 20px 0;'/>" +

                "<p style='font-size: 12px; color: #aaa;'>If you didn’t request this, ignore this email.</p>" +

                "<p style='color: #555;'>Best regards,<br/>The Team</p>" +

                "</div>" +
                "</body>" +
                "</html>";
    }
}
