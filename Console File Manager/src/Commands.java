public enum Commands {
    LIST_OF_FILES("ls"),
    LIST_OF_FILES_WITH_SIZE("ll"),
    CHANGE_DIRECTORY("cd"),
    MAKE_DIRECTORY("mkdir"),
    CREATE_FILE("touch"),
    COPY_FILES("cp"),
    CURRENT_DIRECTORY("pwd"),
    REMOVE("rm"),
    CREATE_ZIP_ARCHIVE("zip"),
    CREATE_EMPTY_ARCHIVE("czip"),
    READ_FILES("read"),
    WRITE_IN_FILES("write"),
    SHOW_COMMANDS("commands"),
    EXIT("exit");

    private final String command;

    Commands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
