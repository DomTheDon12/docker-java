package com.github.dockerjava.cmd.swarm;

import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.LocalNodeState;
import com.github.dockerjava.api.model.SwarmSpec;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LeaveSwarmCmdExecIT extends SwarmCmdIT {

    public static final Logger LOG = LoggerFactory.getLogger(LeaveSwarmCmdExecIT.class);

    @Override
    protected boolean shouldInitializeByDefault() {
        return false;
    }

    @Test
    public void leaveSwarmAsMaster() throws DockerException {
        dockerRule.getClient().initializeSwarmCmd(new SwarmSpec())
                .withListenAddr("127.0.0.1")
                .withAdvertiseAddr("127.0.0.1")
                .exec();

        Info info = dockerRule.getClient().infoCmd().exec();
        LOG.info("Inspected docker: {}", info.toString());

        assertThat(info.getSwarm().getLocalNodeState(), is(LocalNodeState.ACTIVE));

        dockerRule.getClient().leaveSwarmCmd()
                .withForceEnabled(true)
                .exec();
        LOG.info("Left swarm");

        info = dockerRule.getClient().infoCmd().exec();
        LOG.info("Inspected docker: {}", info.toString());

        assertThat(info.getSwarm().getLocalNodeState(), is(LocalNodeState.INACTIVE));

    }

    @Test(expected = DockerException.class)
    public void leavingSwarmThrowsWhenNotInSwarm() throws DockerException {
        dockerRule.getClient().leaveSwarmCmd().exec();
    }

}
