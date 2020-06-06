export interface Index {
  _links: {
    'loot-api:campaigns': Link;
  };
}

export interface Campaigns {
  _embedded: {
    'loot-api:campaign': Campaign[];
  };
  _links: {
    index: Link;
  };
}

export interface Campaign {
  id: number;
  name: string;
  _links: {
    'loot-api:characters': object[];
  };
}

export interface Link {
  href: string;
}



// export interface JsonReturn {
//   _embedded: CharacterList;
// }
//
// export interface CharacterList {
//   'loot-api:character': Characters[];
// }
//
// export interface Characters {
//   id: number;
//   name: string;
// }
//
// export interface Character {
//   name: string;
//   inventory: InventoryItem[];
// }
//
// export interface InventoryItem {
//   quantity: number;
//   name: string;
//   weight: number;
//   details: string;
//   charges: number;
// }
