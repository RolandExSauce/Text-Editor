package com.solideditor.programm.Logging;
import lombok.Getter;

public enum ErrorOrSuccessCode {
    ERR_CODE_1000("1000", "Selected Tab is null"),
    ERR_CODE_1001("1001", "FileInfo is null"),
    ERR_CODE_1002("1002", "Could not add new Tab"),
    ERR_CODE_1003("1003", "Could not create new Tab"),
    ERR_CODE_1004("1004", "Failed to remove unsaved tab"),
    ERR_CODE_1005("1005", "Can't save the existing/new file, File may be deleted from FileSystem"),
    ERR_CODE_1006("1006", "No TabEntry found for the given tab, File may be deleted from FileSystem"),
    ERR_CODE_1007("1007", "Removing Tab with unassociated file path from TabPane"),
    /* SUCCESS CODES **************************************************************************************************/
    SUCCESS_CODE_2000("2000", "SUCCESSFUL APP LAUNCH"), //e.g application successfully launches
    SUCCESS_CODE_2001("2001", "SUCCESSFUL APP SETUP"), //application successfully finishes
    SUCCESS_CODE_2002("2002", "SUCCESSFUL CONSTRUCTOR INITIALIZATION"), //construct init ok
    SUCCESS_CODE_2003("2003", "SUCCESSFUL AGENT LISTENER LAUNCHED"),
    /* UNSUCCESSFUL CODES *********************************************************************************************/
    ERR_CODE_4000("4000", "FAILED TO LOAD FXML FILE RESSOURCE(S)"),
    ERR_CODE_4001("4001", "UNEXPECTED RUNTIME EXCEPTION");

    //add more err codes if needed

    @Getter
    private final String code;

    @Getter
    private final String description;

    ErrorOrSuccessCode(String code, String description) {
        this.code = code;
        this.description = description;
    };

    @Override
    public String toString() {
        return String.format("[%s] %s", code, description);
    }
};
