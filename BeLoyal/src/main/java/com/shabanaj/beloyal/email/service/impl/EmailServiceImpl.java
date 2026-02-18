package com.shabanaj.beloyal.email.service.impl;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.email.dto.SendEmailEvent;
import com.shabanaj.beloyal.email.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final ApplicationEventPublisher publisher;

    @Value("${app.activation.base-url}")
    private String activationBaseUrl;

    public EmailServiceImpl(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void sendActivationEmail(User user, String token) {
        String activationLink = activationBaseUrl + "?token=" + token;

        String subject = "Activate your account";
        String content = buildActivationEmailHtml(user.getFirstName(), activationLink);

        sendMail(user.getEmail(), subject, content);
    }

    @Override
    public void sendBusinessRegistrationEmail(User user, Business business) {
        String subject = "✅ We received your business application";
        String content = buildRegistrationEmailHtml(user, business);

        if(!user.getEmail().equals(business.getBusinessEmail())){
            sendMail(user.getEmail(), subject, content);
            sendMail(business.getBusinessEmail(), subject, content);
        }else{
            sendMail(user.getEmail(), subject, content);
        }
    }

    private String buildRegistrationEmailHtml(User user, Business business) {
        // --- Customize these ---
        String brandName = "Besa Hub";
        String supportEmail = "support@besahub.app";
        String portalUrl = "https://besahub.app/portal"; // replace with your real portal/status URL
        String appId = (business.getId() != null) ? String.valueOf(business.getId()) : "—";

        String ownerName = safe(user.getFirstName(), "there");
        String businessName = safe(business.getBusinessName(), "your business");
        String businessEmail = safe(business.getBusinessEmail(), "—");
        String businessPhone = safe(business.getBusinessPhoneNumber(), "—");
        String businessAddress = safe(business.getAddress(), "—"); // adjust if you have separate fields
        String submittedAt = java.time.LocalDateTime.now().toString(); // or your audited createdAt

        return """
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <meta name="x-apple-disable-message-reformatting">
  <title>Business Application Received</title>
  <!--[if mso]>
  <style type="text/css">
    body, table, td { font-family: Arial, sans-serif !important; }
  </style>
  <![endif]-->
</head>
<body style="margin:0; padding:0; background:#F3F4F6;">
  <div style="display:none; max-height:0; overflow:hidden; opacity:0; color:transparent;">
    Your business application has been recorded and is now pending verification.
  </div>

  <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="background:#F3F4F6; padding: 24px 0;">
    <tr>
      <td align="center" style="padding: 0 16px;">
        <table role="presentation" cellpadding="0" cellspacing="0" width="600"
               style="width:600px; max-width:600px; border-collapse:separate;">
          
          <!-- Header / Banner -->
          <tr>
            <td style="
              background: linear-gradient(135deg, #0EA5E9 0%, #6366F1 55%, #A855F7 100%);
              border-radius: 18px 18px 0 0;
              padding: 26px 28px;
              color:#FFFFFF;">
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <div style="font-size:14px; letter-spacing:0.12em; opacity:0.9; text-transform:uppercase;">
                      %s
                    </div>
                    <div style="font-size:28px; line-height:1.15; font-weight:800; margin-top:6px;">
                      Application received ✅
                    </div>
                    <div style="font-size:15px; line-height:1.5; margin-top:10px; opacity:0.95;">
                      Thanks, %s — we’ve successfully recorded <strong>%s</strong>.
                    </div>
                  </td>
                  <td align="right" style="vertical-align:top;">
                    <div style="
                      display:inline-block;
                      background: rgba(255,255,255,0.18);
                      border: 1px solid rgba(255,255,255,0.30);
                      padding: 10px 12px;
                      border-radius: 999px;
                      font-size: 12px;
                      font-weight: 700;">
                      STATUS: UNDER REVIEW
                    </div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Main Card -->
          <tr>
            <td style="background:#FFFFFF; border-radius:0 0 18px 18px; padding: 24px 28px; box-shadow: 0 10px 25px rgba(17,24,39,0.06);">
              
              <!-- Intro -->
              <div style="font-size:16px; line-height:1.65; color:#111827;">
                Your application is now in our verification queue. Our staff will review your business details
                and confirm eligibility for activation on %s.
              </div>

              <div style="margin-top:14px; padding:14px 14px; background:#F9FAFB; border:1px solid #E5E7EB; border-radius:14px;">
                <div style="font-size:14px; color:#111827; line-height:1.6;">
                  ⏳ <strong>Please note:</strong> Verification can take a little while depending on volume.
                  As soon as a decision is made, you’ll be notified by email.
                </div>
              </div>

              <!-- Details -->
              <div style="margin-top:18px; font-size:13px; color:#6B7280; text-transform:uppercase; letter-spacing:0.08em;">
                Application details
              </div>

              <table role="presentation" width="100%" cellpadding="0" cellspacing="0"
                     style="margin-top:10px; border:1px solid #E5E7EB; border-radius:14px; overflow:hidden; border-collapse:separate;">
                <tr>
                  <td style="padding:12px 14px; background:#F9FAFB; width:40%; color:#374151; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    Business
                  </td>
                  <td style="padding:12px 14px; background:#FFFFFF; color:#111827; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    %s
                  </td>
                </tr>
                <tr>
                  <td style="padding:12px 14px; background:#F9FAFB; color:#374151; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    Application ID
                  </td>
                  <td style="padding:12px 14px; background:#FFFFFF; color:#111827; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    %s
                  </td>
                </tr>
                <tr>
                  <td style="padding:12px 14px; background:#F9FAFB; color:#374151; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    Submitted
                  </td>
                  <td style="padding:12px 14px; background:#FFFFFF; color:#111827; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    %s
                  </td>
                </tr>
                <tr>
                  <td style="padding:12px 14px; background:#F9FAFB; color:#374151; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    Business email
                  </td>
                  <td style="padding:12px 14px; background:#FFFFFF; color:#111827; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    %s
                  </td>
                </tr>
                <tr>
                  <td style="padding:12px 14px; background:#F9FAFB; color:#374151; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    Phone
                  </td>
                  <td style="padding:12px 14px; background:#FFFFFF; color:#111827; font-size:14px; border-bottom:1px solid #E5E7EB;">
                    %s
                  </td>
                </tr>
                <tr>
                  <td style="padding:12px 14px; background:#F9FAFB; color:#374151; font-size:14px;">
                    Address
                  </td>
                  <td style="padding:12px 14px; background:#FFFFFF; color:#111827; font-size:14px;">
                    %s
                  </td>
                </tr>
              </table>

              <!-- Timeline -->
              <div style="margin-top:20px; padding:16px; border-radius:14px; background:#EEF2FF; border:1px solid #E0E7FF;">
                <div style="font-size:15px; font-weight:800; color:#111827; margin-bottom:10px;">
                  What happens next
                </div>
                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border-collapse:separate;">
                  <tr>
                    <td style="vertical-align:top; padding:8px 0; width:28px;">
                      <div style="width:22px; height:22px; border-radius:999px; background:#4F46E5; color:#FFFFFF; font-weight:800; font-size:12px; text-align:center; line-height:22px;">
                        1
                      </div>
                    </td>
                    <td style="padding:7px 0; color:#111827; font-size:14px;">
                      Our team reviews your submitted information.
                    </td>
                  </tr>
                  <tr>
                    <td style="vertical-align:top; padding:8px 0;">
                      <div style="width:22px; height:22px; border-radius:999px; background:#4F46E5; color:#FFFFFF; font-weight:800; font-size:12px; text-align:center; line-height:22px;">
                        2
                      </div>
                    </td>
                    <td style="padding:7px 0; color:#111827; font-size:14px;">
                      We may contact you if we need clarification.
                    </td>
                  </tr>
                  <tr>
                    <td style="vertical-align:top; padding:8px 0;">
                      <div style="width:22px; height:22px; border-radius:999px; background:#4F46E5; color:#FFFFFF; font-weight:800; font-size:12px; text-align:center; line-height:22px;">
                        3
                      </div>
                    </td>
                    <td style="padding:7px 0; color:#111827; font-size:14px;">
                      You’ll receive an email once your application is approved or rejected.
                    </td>
                  </tr>
                </table>
              </div>

              <!-- CTA -->
              <table role="presentation" cellpadding="0" cellspacing="0" style="margin-top:22px;">
                <tr>
                  <td>
                    <a href="%s"
                       style="
                         display:inline-block;
                         background: linear-gradient(135deg, #0EA5E9 0%, #6366F1 55%, #A855F7 100%);
                         color:#FFFFFF;
                         text-decoration:none;
                         padding: 12px 18px;
                         border-radius: 12px;
                         font-weight: 800;
                         font-size: 14px;">
                      View application status
                    </a>
                  </td>
                </tr>
              </table>

              <div style="margin-top:10px; font-size:12px; color:#6B7280; line-height:1.5;">
                If the button doesn’t work, paste this link into your browser:<br>
                <span style="word-break:break-all; color:#4F46E5;">%s</span>
              </div>

              <!-- Footer -->
              <div style="margin-top:22px; padding-top:16px; border-top:1px solid #E5E7EB; font-size:12px; line-height:1.6; color:#6B7280;">
                Need help? Contact us at <a href="mailto:%s" style="color:#4F46E5; text-decoration:none;">%s</a>.
                <br><br>
                — %s Team
              </div>

            </td>
          </tr>

          <!-- Bottom spacing -->
          <tr><td style="height:14px;"></td></tr>

        </table>
      </td>
    </tr>
  </table>
</body>
</html>
"""
                .formatted(
                        brandName,
                        escape(ownerName),
                        escape(businessName),
                        brandName,
                        escape(businessName),
                        escape(appId),
                        escape(submittedAt),
                        escape(businessEmail),
                        escape(businessPhone),
                        escape(businessAddress),
                        portalUrl,
                        portalUrl,
                        supportEmail,
                        supportEmail,
                        brandName
                );
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

    /** Null/blank safe string. */
    private static String safe(String value, String fallback) {
        if (value == null) return fallback;
        String v = value.trim();
        return v.isEmpty() ? fallback : v;
    }

    /** Basic HTML escaping to prevent broken layout / injection. */
    private static String escape(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void sendMail(String to, String subject, String body) {
        publisher.publishEvent(new SendEmailEvent(to, subject, body));
    }
}
