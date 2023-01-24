package com.vulp.tomes.capabilities;

import javax.annotation.Nullable;

public class StarryFormReturn implements IStarryFormReturn {

    private StarryFormReturnHolder holder;

    @Nullable
    @Override
    public StarryFormReturnHolder getHolder() {
        return this.holder;
    }

    @Override
    public void setHolder(StarryFormReturnHolder holder) {
        this.holder = holder;
    }

    @Override
    public void removeHolder() {
        this.holder = null;
    }

    @Override
    public boolean hasHolder() {
        return this.holder != null;
    }

}
