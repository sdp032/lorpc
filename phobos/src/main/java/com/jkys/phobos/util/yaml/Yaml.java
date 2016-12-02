package com.jkys.phobos.util.yaml;

/**
 * Created by zdj on 2016/11/18.
 */
public class Yaml {

    private Representer representer;

    public Yaml() {

    }

    public Yaml(Representer representer) {
        this.representer = representer;
    }

    public String dump(Object o) throws Exception {

        return representer.represent(o);
    }
}
