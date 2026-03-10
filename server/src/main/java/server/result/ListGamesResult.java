package server.result;

import dataaccess.records.GameData;

import java.util.Collection;

public record ListGamesResult(Collection<GameData> games) {
}
