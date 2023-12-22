import { Rank } from "./stats";

export type UserCreationInput = {
  name: string;
  email: string;
  password: string;
  avatarUrl: string;
};

export type UserInfo = {
  id: number;
  name: string;
  avatarUrl?: string;
  role: string;
  rank: Rank;
};

export type UserHome = {
  id: number;
  name: string;
  email: string;
  avatarUrl?: string;
  role: string;
  rank: string;
};

export type UserAvatar = {
  name: string;
  avatarUrl: string;
};

export type UserDetails = {
  id: number;
  name: string;
  email: string;
  avatarUrl: string | null;
  role: string;
  createdAt: string;
};
