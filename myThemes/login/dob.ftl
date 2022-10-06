<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section="form">
        <form method="post" action=${url.loginAction}>
            <div>Date Of Birth:</div><input name="dateOfBirth" type="date" id="dateOfBirth" />
            <br />
            <button type="submit">submit</button>
            <#if messagesPerField.existsError('dateOfBirth')>
                <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                    ${kcSanitize(messagesPerField.getFirstError('dateOfBirth'))?no_esc}
                </span>
            </#if>
        </form>
    </#if>
</@layout.registrationLayout>