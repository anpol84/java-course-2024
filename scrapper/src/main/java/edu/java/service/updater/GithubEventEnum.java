package edu.java.service.updater;

public enum GithubEventEnum {
    COMMIT_COMMENT("CommitCommentEvent", "A commit comment is created."),
    CREATE("CreateEvent", "A Git branch or tag is created."),
    DELETE("DeleteEvent", "A Git branch or tag is deleted."),
    FORK("ForkEvent", "A user forks a repository."),
    GOLLUM("GollumEvent", "A wiki page is created or updated."),
    ISSUE_COMMENT("IssueCommentEvent", "Activity related to an issue or pull request comment."),
    ISSUES("IssuesEvent", "Activity related to an issue."),
    MEMBER("MemberEvent", "Activity related to repository collaborators."),
    PUBLIC("PublicEvent", "Private repository is made public."),
    PULL_REQUEST("PullRequestEvent", "Activity with pull request."),
    PULL_REQUEST_REVIEW("PullRequestReviewEvent", "Activity with review pull request."),
    PULL_REQUEST_REVIEW_COMMENT("PullRequestReviewCommentEvent",
        "Activity with comment in review pull request."),
    PULL_REQUEST_REVIEW_THREAD("PullRequestReviewThreadEvent",
        "Activity related to a comment thread on a pull request being marked as resolved or unresolved."),
    PUSH("PushEvent", "One or more commits are pushed to a repository branch or tag."),
    WATCH("WatchEvent", "Someone stars a repository."),
    UNKNOWN("Unknown event", "Some unknown event");
    private final String type;
    private final String description;

    GithubEventEnum(String type, String description) {
        this.type = type;
        this.description = description;

    }

    public static GithubEventEnum fromType(String type) {
        for (GithubEventEnum eventType : GithubEventEnum.values()) {
            if (eventType.type.equals(type)) {
                return eventType;
            }
        }
        return UNKNOWN;
    }

    public String getDescription() {
        return description;
    }

}
