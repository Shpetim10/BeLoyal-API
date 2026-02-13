package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.activation.base-url}")
    private String activationBaseUrl;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }


    @Override
    public void sendActivationEmail(User user, String token) {
        String activationLink = activationBaseUrl + "?token=" + token;

        String subject = "Activate your account";
        String content = buildActivationEmailHtml(user.getFirstName(), activationLink);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildActivationEmailHtml(String name, String activationLink) {
        String safeName = (name == null || name.isBlank()) ? "there" : name;

        return """
    <!doctype html>
    <html lang="en">
    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>Activate your account</title>
    </head>

    <body style="margin:0;padding:0;background-color:#0B1220;font-family:Arial,Helvetica,sans-serif;">

      <!-- Preheader (hidden preview text) -->
      <div style="display:none;max-height:0;overflow:hidden;opacity:0;color:transparent;">
        Activate your account to start earning points and unlocking rewards.
      </div>

      <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0"
             style="background-color:#0B1220;padding:24px 12px;">
        <tr>
          <td align="center">

            <!-- Outer container -->
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0"
                   style="max-width:640px;background-color:#111827;border-radius:18px;overflow:hidden;
                          border:1px solid rgba(255,255,255,0.08);">

              <!-- HERO -->
              <tr>
                <td style="padding:0;">
                  <div style="
                    background:linear-gradient(135deg,#2563EB 0%%,#1D4ED8 45%%,#0B1220 100%%);
                    padding:30px 28px 18px 28px;">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0">
                      <tr>
                        <td align="left" style="color:#E5E7EB;">
                          <div style="font-size:13px;letter-spacing:0.14em;text-transform:uppercase;opacity:0.92;">
                            BeLoyal Rewards
                          </div>
                          <div style="font-size:28px;line-height:1.25;font-weight:800;margin-top:10px;color:#FFFFFF;">
                            Activate your account
                          </div>
                        </td>

                        <td align="right" style="vertical-align:top;">
                          <!-- Reward coin badge -->
                          <div style="
                            display:inline-block;
                            background-color:rgba(245,158,11,0.16);
                            border:1px solid rgba(245,158,11,0.50);
                            color:#FBBF24;
                            border-radius:999px;
                            padding:10px 12px;
                            font-size:12px;
                            font-weight:800;">
                            ⭐ +100 Welcome Points
                          </div>
                        </td>
                      </tr>
                    </table>

                    <div style="height:1px;background:rgba(255,255,255,0.16);margin-top:18px;"></div>

                    <div style="margin-top:16px;color:#E5E7EB;font-size:15px;line-height:1.7;">
                      Hi <span style="color:#FFFFFF;font-weight:800;">%s</span>,<br/>
                      You’re one click away from earning points, unlocking perks, and collecting rewards.
                    </div>

                    <!-- Progress: 1 step left -->
                    <div style="margin-top:16px;">
                      <div style="font-size:12px;color:#CBD5E1;letter-spacing:0.02em;">
                        Activation progress: <span style="color:#FFFFFF;font-weight:700;">90%%</span>
                        <span style="color:#94A3B8;">(1 step left)</span>
                      </div>
                      <div style="margin-top:8px;background:rgba(255,255,255,0.14);border-radius:999px;height:10px;overflow:hidden;">
                        <div style="width:90%%;height:10px;background:linear-gradient(90deg,#F59E0B 0%%,#FBBF24 45%%,#2563EB 100%%);border-radius:999px;"></div>
                      </div>
                    </div>

                  </div>
                </td>
              </tr>

              <!-- BODY -->
              <tr>
                <td style="padding:24px 28px 12px 28px;background-color:#111827;color:#E5E7EB;">

                  <div style="font-size:16px;line-height:1.75;">
                    Confirm your email address to activate your BeLoyal account.
                  </div>

                  <!-- CTA Button -->
                  <div style="margin-top:20px;margin-bottom:14px;">
                    <a href="%s"
                       style="
                         display:inline-block;
                         background-color:#2563EB;
                         color:#FFFFFF;
                         text-decoration:none;
                         font-weight:800;
                         font-size:15px;
                         padding:14px 22px;
                         border-radius:12px;
                         box-shadow:0 12px 26px rgba(37,99,235,0.30);">
                      Activate Account →
                    </a>
                  </div>

                  <!-- Wallet / points card -->
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-top:10px;">
                    <tr>
                      <td style="
                        padding:14px 14px;
                        border-radius:16px;
                        background:linear-gradient(135deg,rgba(245,158,11,0.18) 0%%, rgba(37,99,235,0.10) 70%%);
                        border:1px solid rgba(255,255,255,0.10);">
                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0">
                          <tr>
                            <td align="left">
                              <div style="font-size:12px;color:#CBD5E1;letter-spacing:0.08em;text-transform:uppercase;">
                                Welcome Bonus
                              </div>
                              <div style="margin-top:6px;font-size:20px;font-weight:900;color:#FFFFFF;line-height:1.2;">
                                100 Points
                              </div>
                              <div style="margin-top:6px;font-size:12.5px;color:#CBD5E1;line-height:1.6;">
                                Activate now to claim your starter points and begin earning more.
                              </div>
                            </td>
                            <td align="right" style="vertical-align:middle;">
                              <div style="
                                width:44px;height:44px;border-radius:14px;
                                background-color:rgba(245,158,11,0.22);
                                border:1px solid rgba(245,158,11,0.55);
                                display:inline-block;
                                text-align:center;
                                line-height:44px;
                                font-size:20px;">
                                🪙
                              </div>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>

                  <!-- Feature bullets (gamified) -->
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-top:14px;">
                    <tr>
                      <td style="
                        padding:14px 14px;
                        border-radius:16px;
                        background-color:rgba(255,255,255,0.04);
                        border:1px solid rgba(255,255,255,0.08);">

                        <div style="font-weight:800;color:#FFFFFF;margin-bottom:8px;font-size:13px;">
                          What happens next
                        </div>

                        <div style="font-size:12.8px;line-height:1.75;color:#CBD5E1;">
                          ✅ Earn points with every scan / purchase<br/>
                          🎁 Redeem rewards instantly when you have enough points<br/>
                          📈 Track your progress with streaks and milestones
                        </div>
                      </td>
                    </tr>
                  </table>

                  <!-- Fallback link -->
                  <div style="margin-top:16px;font-size:12.5px;line-height:1.6;color:#94A3B8;">
                    If the button doesn’t work, copy and paste this link into your browser:
                  </div>

                  <div style="
                    margin-top:10px;
                    padding:12px 12px;
                    border-radius:12px;
                    background-color:#0B1220;
                    border:1px dashed rgba(245,158,11,0.55);
                    word-break:break-all;
                    color:#FBBF24;
                    font-size:12.5px;
                    line-height:1.6;">
                    %s
                  </div>

                  <div style="margin-top:14px;font-size:12.5px;line-height:1.6;color:#94A3B8;">
                    For security, this activation link may expire. If you didn’t create an account, you can safely ignore this email.
                  </div>

                </td>
              </tr>

              <!-- FOOTER -->
              <tr>
                <td style="padding:18px 28px 24px 28px;background-color:#0B1220;color:#94A3B8;">
                  <div style="font-size:12px;line-height:1.7;">
                    Need help? Reply to this email or contact support.<br/>
                    <span style="color:#E5E7EB;">© %d BeLoyal</span>
                  </div>

                  <div style="margin-top:12px;height:1px;background:rgba(255,255,255,0.10);"></div>

                  <div style="margin-top:12px;font-size:11px;line-height:1.6;opacity:0.9;">
                    You’re receiving this because an account was registered with this email address.
                  </div>
                </td>
              </tr>

            </table>
          </td>
        </tr>
      </table>

    </body>
    </html>
    """.formatted(safeName, activationLink, activationLink, java.time.Year.now().getValue());
    }

}
