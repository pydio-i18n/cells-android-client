package com.pydio.android.cells

enum class RemoteType {
    P8, CELLS
}

enum class JobStatus(val id: String) {
    NEW("new"),
    PROCESSING("processing"),
    CANCELLED("cancelled"),
    DONE("done"),
    WARNING("warning"),
    ERROR("error"),
    TIMEOUT("timeout"),
    NO_FILTER("no filter"),
}

enum class LoginStatus(val id: String) {
    // Workaround to store additional destinations as state
    //    String CUSTOM_PATH_ACCOUNTS = "/__acounts__";
    //    String CUSTOM_PATH_BOOKMARKS = "/__bookmarks__";
    //    String CUSTOM_PATH_OFFLINE = "/__offline__";
    //    String CUSTOM_PATH_SHARES = "/__shares__";
    New("new"),
    NoCreds("no-credentials"),
    Unauthorized("unauthorized"),
    Expired("expired"),
    Refreshing("refreshing"),
    Connected("connected"),
}

enum class ListType {
    DEFAULT, TRANSFER, JOB
}
