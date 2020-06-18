export interface Index {
  _links: {
    'loot-api:campaigns': Link;
  };
}

export interface Campaigns {
  _embedded: {
    'loot-api:campaign': EmbeddedCampaign[];
  };
}

export interface EmbeddedCampaign {
  id: number;
  name: string;
  _links: {
    self: Link;
  };
}

export interface Campaign {
  id: number;
  name: string;
  _links: {
    'loot-api:characters': Link;
  };
}

export interface Characters {
  _embedded: {
    'loot-api:character': EmbeddedCharacter[];
  };
}

export interface EmbeddedCharacter {
  id: number;
  name: string;
  _links: {
    self: Link;
  };
}

export interface Character {
  id: number;
  name: string;
  inventory: InventoryItem[];
}

export interface InventoryItem {
  quantity: number;
  name: string;
  weight: number;
  details: string;
  charges: number;
}

export interface Link {
  href: string;
}

