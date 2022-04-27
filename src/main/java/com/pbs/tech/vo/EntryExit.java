package com.pbs.tech.vo;

import com.pbs.tech.model.big.EntryExitEntity;
import com.pbs.tech.model.big.ExitView;

import java.util.List;

public class EntryExit {


    private String name;
    private String id;
    private EntryExitEntity entry_view;
    private List<ExitView> exit_view;



    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String  getName() { return name; }

    public void setName(String name) { this.name = name; }

    public EntryExitEntity getEntry_view() { return entry_view; }

    public void setEntry_view(EntryExitEntity entry_view) { this.entry_view = entry_view; }

    public List<ExitView> getExit_view() { return exit_view; }

    public void setExit_view(List<ExitView> exit_view) { this.exit_view = exit_view; }

}
