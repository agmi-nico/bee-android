package com.apisense.bee.backend.experiment;

import android.os.AsyncTask;
import android.util.Log;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BSenseMobileService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RetrieveExperimentsTask extends AsyncTaskWithCallback<Void, Void, List<Experiment>> {
    private final String TAG = this.getClass().getSimpleName();

    public final static int GET_INSTALLED_EXPERIMENTS = 0;
    public final static int GET_REMOTE_EXPERIMENTS = 1;

    private int retrievalType;

    public RetrieveExperimentsTask(AsyncTasksCallbacks listener, int retrievalType) {
        super(listener);
        this.retrievalType = retrievalType;
    }

    @Override
    protected List<Experiment> doInBackground(Void... params) {
        List<Experiment> gotExperiments;

        if (! APISENSE.apisServerService().isConnected()){
            // Todo: Specific treatment for anonymous user?
            gotExperiments = new ArrayList<Experiment>();
        }else {
            switch (retrievalType) {
                case GET_INSTALLED_EXPERIMENTS:
                    BSenseMobileService mobService = APISENSE.apisense().getBSenseMobileService();
                    Collection exp = mobService.getInstalledExperiments().values();
                    gotExperiments = (exp instanceof List) ? (List) exp : new ArrayList(exp);
                    this.errcode = BeeApplication.ASYNC_SUCCESS;
                    break;
                case GET_REMOTE_EXPERIMENTS:
                    gotExperiments = APISENSE.apisServerService().getRemoteExperiments();
                    this.errcode = BeeApplication.ASYNC_SUCCESS;
                    break;
                default:
                    gotExperiments = new ArrayList<Experiment>();
                    this.errcode = BeeApplication.ASYNC_ERROR;
            }
        }
        Log.d(TAG, "List of experiments returned: " + gotExperiments.toString());
        return gotExperiments;
    }
}