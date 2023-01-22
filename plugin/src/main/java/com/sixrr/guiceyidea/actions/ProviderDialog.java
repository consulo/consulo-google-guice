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

import com.intellij.java.language.psi.JavaPsiFacade;
import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiNameHelper;
import com.intellij.java.language.util.TreeClassChooser;
import com.intellij.java.language.util.TreeClassChooserFactory;
import com.sixrr.guiceyidea.GuiceyIDEABundle;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.FixedSizeButton;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProviderDialog extends DialogWrapper implements DocumentListener{
    private JTextField providerNameField = null;
    private JTextField providedClassField = null;
    private FixedSizeButton classChooserButton = null;
    private final Project project;

    protected ProviderDialog(Project project){
        super(project, true);
        this.project = project;
        setModal(true);
        setTitle("New Guice Provider");
        init();
        validateButtons();
    }

    @NonNls
    protected String getDimensionServiceKey(){
        return "GuiceyIDEA.NewGuiceProvider";
    }

    @Nullable
    protected JComponent createCenterPanel(){
        final JPanel panel = new JPanel(new GridBagLayout());

        final GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.weightx = 1.0;
        gbConstraints.weighty = 0.0;
        gbConstraints.gridwidth = 1;
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        gbConstraints.insets = new Insets(0, 0, 0, 0);
        final JPanel classNamePanel = new JPanel(new BorderLayout());

        final JLabel label1 = new JLabel("Provider Class Name:");
        providerNameField = new JTextField();
        providerNameField.getDocument().addDocumentListener(this);
        label1.setDisplayedMnemonic('P');
        label1.setLabelFor(providerNameField);
        classNamePanel.add(label1, BorderLayout.WEST);
        classNamePanel.add(providerNameField, BorderLayout.CENTER);
        panel.add(classNamePanel, gbConstraints);

        providedClassField = new JTextField();
        providedClassField.getDocument().addDocumentListener(this);
        classChooserButton = new FixedSizeButton(providedClassField);
        classChooserButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
                final TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project).createInheritanceClassChooser(GuiceyIDEABundle.message("select.provided.class"), scope, null, null);

                final String classText = classChooserButton.getText();
                final PsiClass currentClass =
                        JavaPsiFacade.getInstance(project).findClass(classText, GlobalSearchScope.allScope(project));
                if(currentClass != null){
                    chooser.select(currentClass);
                }
                chooser.showDialog();
                final PsiClass selectedClass = chooser.getSelected();
                if(selectedClass != null){
                    final String className = selectedClass.getQualifiedName();
                    providedClassField.setText(className);
                    validateButtons();
                }
            }
        });
        panel.add(classChooserButton, gbConstraints);
        final JPanel existingClassPanel = new JPanel(new BorderLayout());
        final JLabel label2 = new JLabel("Class Provided:");
        label2.setLabelFor(providedClassField);
        label2.setDisplayedMnemonic('C');
        existingClassPanel.add(label2, BorderLayout.WEST);
        existingClassPanel.add(providedClassField, BorderLayout.CENTER);
        existingClassPanel.add(classChooserButton, BorderLayout.EAST);
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 1;
        gbConstraints.gridwidth = 3;
        panel.add(existingClassPanel, gbConstraints);
        return panel;
    }

    public String getProviderName(){
        return providerNameField.getText();
    }

    private void validateButtons(){
        final JavaPsiFacade manager = JavaPsiFacade.getInstance(project);
        final PsiNameHelper nameHelper = manager.getNameHelper();

        final String providerName = getProviderName();
        final String providedClass = getProvidedClass();
        final boolean valid = nameHelper.isIdentifier(providerName) && nameHelper.isQualifiedName(providedClass);
        getOKAction().setEnabled(valid);
    }

    public String getProvidedClass(){
        return providedClassField.getText();
    }

    public void insertUpdate(DocumentEvent event){
        validateButtons();
    }

    public void removeUpdate(DocumentEvent event){
        validateButtons();
    }

    public void changedUpdate(DocumentEvent event){
        validateButtons();
    }
}
