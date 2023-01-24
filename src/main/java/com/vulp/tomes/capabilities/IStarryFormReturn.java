package com.vulp.tomes.capabilities;

import javax.annotation.Nullable;

public interface IStarryFormReturn {

    @Nullable
    StarryFormReturnHolder getHolder();

    void setHolder(StarryFormReturnHolder holder);

    void removeHolder();

    boolean hasHolder();

}
