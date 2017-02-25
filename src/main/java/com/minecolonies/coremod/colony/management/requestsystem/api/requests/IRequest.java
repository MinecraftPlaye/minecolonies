package com.minecolonies.coremod.colony.management.requestsystem.api.requests;

import com.google.common.collect.ImmutableCollection;
import com.minecolonies.coremod.colony.IColony;
import com.minecolonies.coremod.colony.management.requestsystem.api.IRequestManager;
import com.minecolonies.coremod.colony.management.requestsystem.api.IRequestToken;
import com.minecolonies.coremod.colony.management.requestsystem.api.RequestState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Used to represent requests, of type R, made to the internal market of the colony.
 * @param <R> The type of request, eg ItemStack, FluidStack etc.
 */
public interface IRequest<R> {



    /**
     * The unique token representing the request outside of the management system.
     * @return the token representing the request outside of the management system.
     */
    IRequestToken getToken();

    /**
     * Used to determine which type of request this is.
     * Only RequestResolvers for this Type are then used to resolve the this.
     * @return The class that represents this Type of Request.
     */
    @NotNull
    Class<? extends R> getRequestType();

    /**
     * Returns the current state of the request.
     * @return The current state.
     */
    @NotNull
    RequestState getState();

    /**
     * Setter for the current state of this request.
     *
     * The implementing class is responsible for propagating the state change to its parent, if needed.
     *
     * @param state The new state of this request.
     * @param manager The request manager that updated the state.
     */
    void setState(@NotNull IRequestManager manager, @NotNull RequestState state);

    /**
     * Return the object that is actually requested.
     * A RequestResolver can compare this object however way it sees fit.
     *
     * During the resolving process this object is called multiple times. But at least twice.
     * A cached implementation is preferred.
     * @return The object that is actually requested.
     */
    @NotNull
    R getRequest();

    /**
     * Returns the result of this request.
     * @return The result of this request, or null if it is not available.
     */
    @Nullable
    R getResult();

    /**
     * Setter for the result of the request.
     * @param result The new result of this request.
     */
    void setResult(@NotNull R result);

    /**
     * Returns the colony this request was made in.
     * @return the colony this request was made in.
     */
    @NotNull
    IColony getColony();

    /**
     * Returns the parent of this request.
     * If this is set it means that this request is part of request chain.
     *
     * @return The parent of this request, or null if it has no parent.
     */
    @Nullable
    IRequestToken getParent();

    /**
     * Method used to set the parent of a request.
     * @param parent The new parent, or null to clear the existing one.
     */
    void setParent(@Nullable IRequestToken parent);

    /**
     * Returns true if this request has a parent, false if not.
     * @return true if this request has a parent, false if not.
     */
    boolean hasParent();

    /**
     * Method used to add a single Child.
     * @param child The new child request to add.
     */
    void addChild(@NotNull IRequestToken child);

    /**
     * Method to add multiple children in a single call.
     * @param children An array of children to add.
     */
    void addChildren(@NotNull IRequestToken... children);

    /**
     * Method to add multiple children in a single call.
     * @param children A collection of children to add.
     */
    void addChildren(@NotNull Collection<IRequestToken> children);

    /**
     * Method used to remove a single Child.
     * @param child The new child request to remove.
     */
    void removeChild(@NotNull IRequestToken child);

    /**
     * Method to remove multiple children in a single call.
     * @param children An array of children to remove.
     */
    void removeChildren(@NotNull IRequestToken... children);

    /**
     * Method to remove multiple children in a single call.
     * @param children A collection of children to remove.
     */
    void removeChildren(@NotNull Collection<IRequestToken> children);

    /**
     * Method to check if this request has children.
     * @return
     */
    boolean hasChildren();

    /**
     * Method to get the children of this request.
     * Immutable.
     * @return An immutable collection of the children of this request.
     */
    @NotNull
    ImmutableCollection<IRequestToken> getChildren();

    /**
     * Method called by a child state to indicate that its state has been updated.
     *
     * @param manager The manager that caused the update on the child.
     * @param child The child that was updated.
     */
    void childStateUpdated(@NotNull IRequestManager manager, @NotNull IRequestToken child);
}
