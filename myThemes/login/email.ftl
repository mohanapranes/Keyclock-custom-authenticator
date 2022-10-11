<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section="form">
        <form method="post" action=${url.loginAction}>
            <div>To Email:</div><input name="email" type="email" id="email" />
            <br />
            <button type="submit">submit</button>
            <#if messagesPerField.existsError('email')>
                <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                    ${kcSanitize(messagesPerField.getFirstError('email'))?no_esc}
                </span>
            </#if>
        </form>
    </#if>
</@layout.registrationLayout>