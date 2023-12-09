import { BoardType, Player, Stone } from "./board";

export type MatchCreationInputModel = {
  isPrivate: boolean;
  size: number;
  variant: string;
};

export type MatchCreationOutPutModel = {
  id: string;
  state: "SETUP" | "ONGOING" | "FINISHED";
};

export type Match = {
  id: string;
  isPrivate: boolean;
  variant: string;
  blackId: number;
  whiteId: number;
  state: "SETUP" | "ONGOING" | "FINISHED";
  board: BoardType;
};

export type PlayOutputModel = {
  stone: Stone;
  matchState: string;
  turn: Player;
};