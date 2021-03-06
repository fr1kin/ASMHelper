package com.fr1kin.asmhelper.types;

/**
 * Created on 1/5/2017 by fr1kin
 *
 * For any types that are derived under a class (fields, methods, anything else)
 */
public abstract class ASMClassMember implements IASMType {
    private final String name;
    private final ASMClass parent;
    private final boolean isStatic;

    /**
     * Sets this test parent
     * @param name name of the derived object
     * @param parent parent class to what this class is representing
     */
    public ASMClassMember(String name, ASMClass parent, boolean isStatic) {
        this.name = name;
        this.parent = parent;
        this.isStatic = isStatic;
    }

    /**
     * Gets the objects name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the parent class
     * @return parent class
     */
    public ASMClass getParentClass() {
        return parent;
    }

    public abstract String getDescriptor();

    /**
     * Check if the member is static
     * @return if member is static to the class or not
     */
    public boolean isStatic() {
        return isStatic;
    }

    public abstract String toString();

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ASMClassMember) {
            return this.getName().equals(((ASMClassMember) obj).getName()) && this.getParentClass().equals(((ASMClassMember) obj).getParentClass());
        } else return false;
    }
}
