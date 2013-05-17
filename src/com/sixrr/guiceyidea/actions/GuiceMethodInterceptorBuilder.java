/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sixrr.guiceyidea.actions;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.util.Properties;

public class GuiceMethodInterceptorBuilder{
    private String className = null;

    public void setClassName(String className){
        this.className = className;
    }

    public String buildInterceptorClass() throws IOException{
        @NonNls final StringBuilder out = new StringBuilder(1024);
        final FileTemplateManager templateManager = FileTemplateManager.getInstance();
        FileTemplate headerTemplate;
        try{
            headerTemplate = templateManager.getInternalTemplate(FileTemplateManager.FILE_HEADER_TEMPLATE_NAME);
        } catch(Exception e){
            headerTemplate = null;
        }
        final Properties defaultProperties = templateManager.getDefaultProperties();
        @NonNls final Properties properties = new Properties(defaultProperties);
        properties.setProperty("PACKAGE_NAME", "www.bullshit.com");
        properties.setProperty("NAME", className);
        if(headerTemplate != null){
            @NonNls final String headerText = headerTemplate.getText(properties);

            final String cleanedText = headerText.replace("public file header " + className + " { }", "");
            out.append(cleanedText);
        }
        out.append('\n');
        out.append("public class ");
        out.append(className);
        out.append(" implements org.aopalliance.intercept.MethodInterceptor{\n");
        out.append("    public Object invoke(org.aopalliance.intercept.MethodInvocation methodInvocation) throws Throwable{\n");
        out.append("        return methodInvocation.proceed();\n");
        out.append("    }\n");
        out.append("}\n");

        return out.toString();
    }
}