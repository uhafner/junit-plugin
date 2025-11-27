package hudson.tasks.junit.pipeline;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
public class Java25ITest {
    private JenkinsRule jenkins;

    @BeforeEach
    public void setUp(final JenkinsRule jenkins) {
        this.jenkins = jenkins;
    }

    @Test
    void shouldRunJUnit() throws Exception {
        var project = jenkins.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(
                """
                node {
                    echo 'Hello Job';
                    junit '**/target/surefire-reports/TEST-*.xml';
                }
                """, true));
        jenkins.buildAndAssertSuccess(project);
    }
}
