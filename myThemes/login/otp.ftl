<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section="form">
        <form method="post" action=${url.loginAction}> 
            <div>OTP:</div><input name="otp" type="text" id="otp" />
            <br />
            <button type="submit">submit</button>
            <#if messagesPerField.existsError('otp')>
                <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                    ${kcSanitize(messagesPerField.getFirstError('otp'))?no_esc}
                </span>
            </#if>
        </form>
    </#if>
</@layout.registrationLayout>