package org.dotnet.ci.triggers;

import jobs.generation.JobReport
import jobs.generation.Utilities

// Covers a generic triggers in Jenkins
// Periodic - Periodic triggers against a Git repo
class GenericTriggerBuilder implements TriggerBuilder {
    public enum TriggerType {
        PERIODIC
    }

    // Periodic
    // Cron string representing the execution period 
    private String _cronString
    // Always run regardless of the commit state
    private boolean _alwaysRun = false
    // Trigger TriggerType
    private TriggerType _triggerType

    // Returns true if the trigger is a PR (private or PR branch) SCM, vs. 'official'
    public boolean isPRTrigger() {
        return false
    }

    private GenericTriggerBuilder(TriggerType triggerType) {
        this._triggerType = triggerType
    }

    // Constructs a new periodic trigger
    // Parameters:
    //  cronString - Cron string to run the job on
    // Returns:
    //  a new periodic trigger that runs on the specified interval
    def static GenericTriggerBuilder triggerPeriodically(String cronString) {
        def newTrigger = new GenericTriggerBuilder(TriggerType.PERIODIC)
        newTrigger._cronString = cronString
        return newTrigger
    }
    
    // Forces the periodic trigger to run regardless of source change
    def alwaysRunPeriodicTrigger() {
        assert this._triggerType == TriggerType.PERIODIC
        this._alwaysRun = true
    }

    // Emits the trigger for the specific job
    // Parameters:
    //  job - Job to emit trigger for
    void emitTrigger(def job) {
        assert this._triggerType == TriggerType.PERIODIC

        job.with {
            triggers {
                if (this._alwaysRun) {
                    cron(this._cronString)
                }
                else {
                    scm(this._cronString)
                }
            }
        }

        JobReport.Report.addCronTriggeredJob(job.name, this._cronString, this._alwaysRun)
    }
}