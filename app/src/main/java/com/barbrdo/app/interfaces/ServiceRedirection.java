package com.barbrdo.app.interfaces;

public interface ServiceRedirection {

    /**
     * The interface method implemented in the java files
     *
     * @param taskID the id based on which the relevant action is performed
     * @return none
     */

    void onSuccessRedirection(int taskID);

    /**
     * The interface method implemented in the java files
     *
     * @param errorMessage the error message to be displayed
     * @return none
     */
    void onFailureRedirection(String errorMessage);
}
