//
// Created by Jordan on 9/29/2017.
//
class Mapper;
class APU{
public:
    int cyclenum;
    Mapper* map;
    APU(Mapper* m){
        map = m;
    };
    void writeRegister(int index, uint8_t b){};
    uint8_t readRegisters(int index){};

};
