package com.github.novotnyr.idea.gitlab.quickmr;

import com.github.novotnyr.idea.git.GitService;
import com.github.novotnyr.idea.gitlab.quickmr.settings.Settings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

import java.util.List;
import java.util.Optional;

public class PlaceholderResolver {
    public static final String LAST_COMMIT_MESSAGE_PLACEHOLDER = "lastCommitMessage";
    public static final String LAST_COMMIT_MESSAGE_SUBJECT_PLACEHOLDER = "lastCommitMessageSubject";
    public static final String LAST_COMMIT_MESSAGE_BODY_PLACEHOLDER = "lastCommitMessageBody";
    public static final String SOURCE_BRANCH_PLACEHOLDER = "sourceBranch";
    public static final String TARGET_BRANCH_PLACEHOLDER = "targetBranch";

    public static final String[] PLACEHOLDERS = {
        LAST_COMMIT_MESSAGE_PLACEHOLDER,
        LAST_COMMIT_MESSAGE_SUBJECT_PLACEHOLDER,
        LAST_COMMIT_MESSAGE_BODY_PLACEHOLDER,
        SOURCE_BRANCH_PLACEHOLDER,
        TARGET_BRANCH_PLACEHOLDER
    };

    private final GitService gitService;
    private final Project project;
    private final Settings settings;
    private SelectedModule selectedModule;
    private String targetBranch = "";

    public PlaceholderResolver(GitService gitService, Project project, Settings settings) {
        this.gitService = gitService;
        this.project = project;
        this.settings = settings;
    }
    public SelectedModule getSelectedModule() {
        return selectedModule;
    }

    public void setSelectedModule(SelectedModule selectedModule) {
        this.selectedModule = selectedModule;
    }

    public void setTargetBranch(String targetBranch) {
        if (StringUtil.isEmpty(targetBranch)) {
            if (this.settings.getDefaultTargetBranch().contains(",")){
                List<String> split = StringUtil.split(this.settings.getDefaultTargetBranch(), ",");
                this.targetBranch = split.get(0);
            } else {
                this.targetBranch = this.settings.getDefaultTargetBranch();
            }
        } else {
            this.targetBranch = targetBranch;
        }
    }

    public String resolvePlaceholder(final String template, String placeholder, NewMergeRequest newMergeRequest) {
        String buffer = template;
        if (LAST_COMMIT_MESSAGE_PLACEHOLDER.equalsIgnoreCase(placeholder.trim())) {
            Optional<String> maybeLastCommitMessage = gitService.getLastCommitMessage(this.project, this.selectedModule);
            if(maybeLastCommitMessage.isPresent()) {
                String lastCommitMessage = maybeLastCommitMessage.get();
                buffer = buffer.replaceAll("\\{\\{lastCommitMessage}}", lastCommitMessage);
            }
        }
        if (LAST_COMMIT_MESSAGE_SUBJECT_PLACEHOLDER.equalsIgnoreCase(placeholder.trim())) {
            Optional<String> subject = gitService.getLastCommitMessageSubject(this.project, this.selectedModule);
            if(subject.isPresent()) {
                buffer = buffer.replaceAll("\\{\\{lastCommitMessageSubject}}", subject.get());
            }
        }
        if (LAST_COMMIT_MESSAGE_BODY_PLACEHOLDER.equalsIgnoreCase(placeholder.trim())) {
            Optional<String> body = gitService.getLastCommitMessageBody(this.project, this.selectedModule);
            if(body.isPresent()) {
                buffer = buffer.replaceAll("\\{\\{lastCommitMessageBody}}", body.get());
            }
        }
        if (SOURCE_BRANCH_PLACEHOLDER.equalsIgnoreCase(placeholder.trim())) {
            buffer = buffer.replaceAll("\\{\\{sourceBranch}}", newMergeRequest.getSourceBranch());
        }
        if (TARGET_BRANCH_PLACEHOLDER.equalsIgnoreCase(placeholder.trim())) {
            buffer = buffer.replaceAll("\\{\\{targetBranch}}", this.targetBranch);
        }

        return buffer;
    }
}
