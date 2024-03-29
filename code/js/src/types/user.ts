import userData from "../data/userData";
import { fetchAPI } from "../utils/http";
import { SirenEntity } from "./siren";
import { Rank, UserStats } from "./stats";

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

export class UserDetailsSiren {
  constructor(userSiren: SirenEntity<UserDetails>) {
    this.userSiren = userSiren;
  }

  private userSiren: SirenEntity<UserDetails>;

  async updateUser(name: string, avatarUrl?: string): Promise<SirenEntity<UserDetails>> {
    const updateUserAction = userData.getUpdateUserAction(this.userSiren);
    const body = {
      name,
      avatarUrl,
    };
    return await fetchAPI<UserDetails>(
      updateUserAction.href,
      updateUserAction.method,
      body
    );
  }

  async fetchStats(): Promise<SirenEntity<UserStats>> {
    return await fetchAPI(userData.getStatsHref(this.userSiren));
  }
}
