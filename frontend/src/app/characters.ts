export interface JsonReturn {
  _embedded: CharacterList;
}

export interface CharacterList {
  'loot-api:character': Characters[];
}

export interface Characters {
  id: number;
  name: string;
}

export interface Character {
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
