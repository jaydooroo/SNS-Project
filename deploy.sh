#!/bin/bash
arr=(
        "GetFollowerCount"
        "GetUserRequest"
        "GetFollowingRequest"
        "LogoutRequest"
        "UnfollowRequest"
        "GetFeedRequest"
        "RegisterRequest"
        "LoginRequest"
        "GetFollowingCount"
        "PostStatusRequest"
        "GetStoryRequest"
        "GetFollowersRequest"
        "FollowRequest"
        "IsFollowerRequest"
    )
for FUNCTION_NAME in "${arr[@]}"
do
  echo "Name: $FUNCTION_NAME"
  aws lambda update-function-code --function-name $FUNCTION_NAME --zip-file 'C:\cs340\tweeter-original\server\build\libs\server-all.jar' &
done