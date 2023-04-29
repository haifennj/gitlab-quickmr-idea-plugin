package com.github.novotnyr.idea.gitlab.quickmr;

import com.github.novotnyr.idea.gitlab.quickmr.settings.Settings;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreateMergeRequestToBranchGroup extends ActionGroup {
    public CreateMergeRequestToBranchGroup() {
        super("Quick Merge Request to", true);
    }

    @NotNull
    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent anActionEvent) {
        if (anActionEvent == null) {
            return new AnAction[0];
        }
        Project project = anActionEvent.getProject();
        if (project == null) {
            return new AnAction[0];
        }
        Settings settings = project.getService(Settings.class);

        List<AnAction> actions = new ArrayList<>();
        List<String> branches = StringUtil.split(settings.getDefaultTargetBranch(), ",");
        for (String branch : branches) {
            CreateMergeRequestAction action = new CreateMergeRequestAction(branch);
            action.setTargetBranch(branch);
            actions.add(action);
        }
        return actions.toArray(new AnAction[0]);
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        Settings settings = project.getService(Settings.class);
        e.getPresentation().setEnabledAndVisible(settings.getDefaultTargetBranch().contains(","));
    }
}
